package securityproject.service

/**
  * Created by Alfred on 12.01.2021.
  */

import java.time.LocalDateTime

import securityproject.AppConfig
import securityproject.auth.{AuthToken, JwtTokenClaim, JwtTokenService, PasswordHash}
import securityproject.exception.AuthenticationException
import securityproject.model.auth.AuthRequest
import securityproject.model.user.{User, UserDAO}
import securityproject.validator.SessionValidator

import scala.concurrent.{ExecutionContext, Future}

trait SessionService {
  protected val userDAO: UserDAO
  protected val jwtTokenService: JwtTokenService
  protected val loginCountThreshold: Int

  def authenticate(request: AuthRequest)(implicit ec: ExecutionContext): Future[AuthToken] = {
    // fixme validate outside of the service
    for {
      (username, password) <- SessionValidator.validateAuthRequest(request)
      user <- authenticateUser(username, password)
    } yield buildNewToken(user)
  }

  private def buildNewToken(user: User): AuthToken = {
    val expirationDate = LocalDateTime.now().plusDays(1)
    val token = jwtTokenService.generateToken(JwtTokenClaim(user.id.get, user.username, expirationDate))
    AuthToken(user.id.get, user.username, token)
  }


  private def authenticateUser(username: String, password: Array[Byte])(implicit ec: ExecutionContext): Future[User] = {
    userDAO.getByUsername(username).flatMap {
      case Some(user) =>
        if (user.loginCount == 3) {
          Future.failed(AuthenticationException("Exceeded maximum number of logins"))
        }
        else if (isPasswordValid(user, password)) {
          userDAO.insertOrUpdate(user.copy(loginCount = 0)).map(_ => user)
        } else {
          userDAO.insertOrUpdate(user.copy(loginCount = user.loginCount + 1))
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
    jwtTokenService.decode(token).toOption.map(claim => AuthToken(claim.userId, claim.username, token))
  }
}

object SessionService extends SessionService {
  override protected val userDAO: UserDAO = UserDAO
  override protected val jwtTokenService: JwtTokenService = JwtTokenService
  override protected val loginCountThreshold: Int = AppConfig.getInt("loginCountThreshold")
}