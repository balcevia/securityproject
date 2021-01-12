package securityproject

/**
  * Created by Alfred on 12.01.2021.
  */
import java.util.Date

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{StatusCode, StatusCodes}
import akka.http.scaladsl.server.{Directive1, Route}
import akka.http.scaladsl.unmarshalling.{FromRequestUnmarshaller, Unmarshaller}
import akka.http.scaladsl.server.Directives._
import securityproject.AppRejectionHandler.AuthFailedRejection
import securityproject.api.{ErrorResponse, OkResponse}
import securityproject.auth.AuthToken
import securityproject.service.SessionService
import securityproject.utils.JsonUtils

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

trait AppDirectives {

  implicit val executionContext: ExecutionContext =  AppActorSystem.executionContext
  implicit val actorSystem: ActorSystem = AppActorSystem.system

  private def rightNow(): String = new Date(System.currentTimeMillis()).toString

  implicit def json4sUnmarshaller[T: Manifest]: FromRequestUnmarshaller[T] = {
    Unmarshaller(_ => request => {
      Unmarshaller.stringUnmarshaller(request.entity).map(JsonUtils.parse[T])
    })
  }

  def completeFuture[T](future: => Future[T]): Route = onComplete(future) {
    case Success(result) => ok(result)
    case Failure(exception) => throw exception
  }

  def ok[T](body: T): Route = {
    val response = OkResponse(body, rightNow())
    complete(StatusCodes.OK, JsonUtils.format(response))
  }

  def error(exception: Throwable, statusCode: StatusCode): Route = {
    val response = ErrorResponse(exception.getMessage, rightNow())
    complete(statusCode, JsonUtils.format(response))
  }

  def extractToken: Directive1[Option[AuthToken]] = {
    optionalHeaderValueByName("Authorization").flatMap {
      case Some(authToken) => provide(SessionService.getToken(authToken))
      case None => provide(None)
    }
  }

  def authenticatedRequests: Directive1[AuthToken] = {
    extractToken flatMap {
      case Some(token) => provide(token)
      case _ => reject(AuthFailedRejection("No valid user found!"))
    }
  }

}

object AppDirectives extends AppDirectives