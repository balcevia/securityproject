package securityproject.api.routes

import securityproject.AppDirectives
import securityproject.auth.AuthToken
import securityproject.model.user.{UserDTO, UserPostRequest}
import securityproject.service.UserService
import securityproject.validator.UserValidator

import scala.concurrent.Future

/**
  * Created by Alfred on 12.01.2021.
  */
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._

object UserRoutes extends AppDirectives {

  def routes: Route = concat(
    path("user") {
      post {
        entity(as[UserPostRequest]) { request =>
          completeFuture {
            createUser(request)
          }
        }
      }
    },
    path("user-list") {
      get {
        authenticatedRequests { token =>
          completeFuture{
            getListOfUsers(token)
          }
        }
      }
    }
  )

  def createUser(request: UserPostRequest): Future[Unit] = {
    for {
      validRequest <- UserValidator.validatePostRequest(request)
      _ <- UserService.createNewUser(validRequest)
    } yield()
  }

  def getListOfUsers(token: AuthToken): Future[Seq[UserDTO]] = {
    UserService.getListOfUsers(token.userId)
  }

}
