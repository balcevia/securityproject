package securityproject

/**
  * Created by Alfred on 12.01.2021.
  */
import akka.http.javadsl.server
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Rejection, RejectionHandler, ValidationRejection}

object AppRejectionHandler {

  import AppDirectives._

  def handler: RejectionHandler = RejectionHandler.newBuilder()
    .handleNotFound {
      error(new Exception("Unknown Resource!"), StatusCodes.NotFound)
    }.handle {
    case ValidationRejection(msg, _) =>
      error(new Exception(msg), StatusCodes.BadRequest)
    case AuthFailedRejection(message) =>
      error(new Exception(message), StatusCodes.Unauthorized)
  }.result()

  case class AuthFailedRejection(message: String) extends server.AuthorizationFailedRejection with Rejection
}