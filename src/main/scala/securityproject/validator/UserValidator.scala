package securityproject.validator

import securityproject.model.user.UserPostRequest

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by Alfred on 12.01.2021.
  */
trait UserValidator extends ValidatorBase {

  def validatePostRequest(request: UserPostRequest)(implicit ec: ExecutionContext): Future[UserPostRequest] = {
    for {
      _ <- validateRequired(request.username, "Username is required")
      _ <- validateRequired(request.password, "Password is required")
    } yield request
  }
}

object UserValidator extends UserValidator
