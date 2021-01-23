package securityproject.api.routes

import akka.http.scaladsl.server.Route
import securityproject.AppDirectives
import akka.http.scaladsl.server.Directives._
import securityproject.auth.AuthToken
import securityproject.model.loginActivity.LoginActivity
import securityproject.service.LoginActivityService

import scala.concurrent.Future

object LoginActivityRoutes extends AppDirectives {

  def routes: Route =
    path("activity") {
      get {
        authenticatedRequests { token =>
          completeFuture {
            getActivity(token)
          }
        }
      }
    }

  def getActivity(token: AuthToken): Future[Seq[LoginActivity]] = {
    LoginActivityService.getActivityForUser(token.userId)
  }
}
