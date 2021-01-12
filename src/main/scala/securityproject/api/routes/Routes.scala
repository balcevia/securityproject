package securityproject.api.routes

/**
  * Created by Alfred on 12.01.2021.
  */
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import securityproject.{AppExceptionHandler, AppRejectionHandler}

object Routes {

  val routes: Route =
    handleExceptions(AppExceptionHandler.handler) {
      handleRejections(AppRejectionHandler.handler) {
        concat(
          SessionRoutes.routes,
          UserRoutes.routes
        )
      }
    }
}