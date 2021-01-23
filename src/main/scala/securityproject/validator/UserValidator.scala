package securityproject.validator

import securityproject.AppConfig
import securityproject.model.user.UserPostRequest
import securityproject.service.UserService

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by Alfred on 12.01.2021.
  */
trait UserValidator extends ValidatorBase {

  protected val entropyThreshold: Int
  protected val userService: UserService

  def validatePostRequest(request: UserPostRequest)(implicit ec: ExecutionContext): Future[UserPostRequest] = {
    for {
      email <- validateRequired(request.email, "Username is required")
      password <- validateRequired(request.password, "Password is required")
      _ <- validatePassword(password)
      _ <- validateEmail(email)
      _ <- validateEmailUniqueness(email)
    } yield request
  }

  private def validateEmail(email: String): Future[String] = {
    val emailRegex = """^[a-zA-Z0-9.!#$%&’*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$""".r
    if(emailRegex.findFirstMatchIn(email).isDefined) {
      Future.successful(email)
    } else {
      Future.failed(new Exception("Email address is not valid"))
    }
  }

  private def validateEmailUniqueness(email: String)(implicit ec: ExecutionContext): Future[Unit] = {
    userService.getByEmail(email).flatMap {
      case Some(_) => Future.failed(new Exception(s"User with email $email already exists"))
      case None => Future.successful(())
    }
  }

  private def validatePassword(password: Array[Byte]): Future[Array[Byte]] = {
    import securityproject.utils.MathUtils._

    val charArray = password.map(_.toChar)
    val poolOfUniqueCharacters = UserValidator.passwordConditions.map(c => c(charArray)).sum

    val passwordEntropy = entropy(poolOfUniqueCharacters, password.length)

    if(passwordEntropy >= entropyThreshold) {
      Future.successful(password)
    } else {
      Future.failed(new Exception("Password is weak"))
    }
  }
}

object UserValidator extends UserValidator {
  override protected val entropyThreshold: Int = AppConfig.getInt("auth.jwt.entropyThreshold")
  override protected val userService: UserService = UserService

  private val lowerUpperCasePoolLength = 26
  private val numericPoolLength = 10
  private val specialCharPoolLength = 9

  private val passwordConditions = List(
    (chars: Array[Char]) => if (chars.exists(_.isLower)) lowerUpperCasePoolLength else 0,
    (chars: Array[Char]) => if (chars.exists(_.isUpper)) lowerUpperCasePoolLength else 0,
    (chars: Array[Char]) => if (chars.exists(_.isDigit)) numericPoolLength else 0,
    (chars: Array[Char]) => if (chars.exists(!_.isLetterOrDigit)) specialCharPoolLength else 0
  )
}