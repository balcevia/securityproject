package securityproject.service

import java.time.LocalDateTime

import securityproject.AppConfig
import securityproject.auth.{AuthToken, JwtTokenClaim, JwtTokenService, PasswordHash}
import securityproject.exception.AuthenticationException
import securityproject.model.auth.AuthRequest
import securityproject.model.user.{User, UserDAO}
import securityproject.validator.SessionValidator

import scala.concurrent.{ExecutionContext, Future}

trait SessionService {

  import securityproject.AppActorSystem.system

  protected val userService: UserService
  protected val loginActivityService: LoginActivityService
  protected val jwtTokenService: JwtTokenService
  protected val loginCountThreshold: Int
  protected val loginDelayMilliseconds: Int

  def authenticate(username: String, password: Array[Byte], ipAddress: String)(implicit ec: ExecutionContext): Future[AuthToken] = {
    authenticateUser(username, password, ipAddress).map(buildNewToken)
  }

  private def buildNewToken(user: User): AuthToken = {
    val expirationDate = LocalDateTime.now().plusDays(1)
    val token = jwtTokenService.generateToken(JwtTokenClaim(user.id.get, user.email, expirationDate))
    AuthToken(user.id.get, user.email, token)
  }


  private def authenticateUser(username: String, password: Array[Byte], ipAddress: String)(implicit ec: ExecutionContext): Future[User] = {
    import scala.concurrent.duration._

    userService.getByEmail(username).flatMap {
      case Some(user) =>
        if (user.invalidLoginCount == loginCountThreshold) {
          akka.pattern.after(loginDelayMilliseconds.millis)(Future.failed(AuthenticationException("Exceeded maximum number of logins")))
        }
        else if (isPasswordValid(user, password)) {
          for {
            u <- userService.insertOrUpdate(user.copy(invalidLoginCount = 0)).map(_ => user)
            _ <- loginActivityService.addActivity(u.id.get, ipAddress)
          } yield u
        } else {
          userService.insertOrUpdate(user.copy(invalidLoginCount = user.invalidLoginCount + 1))
            .flatMap(_ => Future.failed(AuthenticationException("Username or password is invalid")))
        }
      case None => Future.failed(AuthenticationException("Username or password is invalid"))
    }
  }

  private def isPasswordValid(user: User, password: Array[Byte]): Boolean = {
    val passwordHash = PasswordHash.generateHash(password, user.salt)
    passwordHash.deep == user.password.deep
  }

  def getToken(token: String): Option[AuthToken] = {
    jwtTokenService.decode(token).toOption.map(claim => AuthToken(claim.userId, claim.email, token))
  }
}

object SessionService extends SessionService {
  override protected val userService: UserService = UserService
  override protected val loginActivityService: LoginActivityService = LoginActivityService
  override protected val jwtTokenService: JwtTokenService = JwtTokenService
  override protected val loginCountThreshold: Int = AppConfig.getInt("loginCountThreshold")
  override protected val loginDelayMilliseconds: Int = AppConfig.getInt("loginDelayMilliseconds")
}