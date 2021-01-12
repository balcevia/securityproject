package securityproject

/**
  * Created by Alfred on 12.01.2021.
  */
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.ExceptionHandler
import securityproject.exception.AuthenticationException

object AppExceptionHandler {
  import AppDirectives.error

  def handler = ExceptionHandler {
    case e: AuthenticationException =>
      error(e, StatusCodes.Unauthorized)
    case e: Throwable =>
      error(e, StatusCodes.BadRequest)
  }
}