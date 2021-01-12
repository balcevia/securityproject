package securityproject.validator

/**
  * Created by Alfred on 12.01.2021.
  */
import securityproject.model.auth.AuthRequest

import scala.concurrent.{ExecutionContext, Future}

trait SessionValidator extends ValidatorBase {

  def validateAuthRequest(authRequest: AuthRequest)(implicit ec: ExecutionContext): Future[(String, Array[Byte])] = {
    for {
      username <- validateRequired(authRequest.username, "Username is required")
      password <- validateRequired(authRequest.password, "Password, is required")
    } yield (username, password)
  }
}

object SessionValidator extends SessionValidator