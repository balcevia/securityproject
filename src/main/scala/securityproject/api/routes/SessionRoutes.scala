package securityproject.api.routes

/**
  * Created by Alfred on 12.01.2021.
  */
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import securityproject.AppDirectives
import securityproject.auth.AuthToken
import securityproject.model.auth.AuthRequest
import securityproject.service.SessionService

import scala.concurrent.Future

object SessionRoutes extends AppDirectives {

  def routes: Route = {
    path("auth") {
      post {
        entity(as[AuthRequest]) { request =>
          completeFuture {
            authenticateUser(request)
          }
        }
      }
    }
  }

  def authenticateUser(request: AuthRequest): Future[AuthToken] = {
    SessionService.authenticate(request)
  }

}
