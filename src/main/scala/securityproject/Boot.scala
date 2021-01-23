package securityproject

/**
  * Created by Alfred on 12.01.2021.
  */
import akka.actor.ActorSystem
import akka.http.scaladsl.Http

import scala.concurrent.ExecutionContext
import com.typesafe.scalalogging.LazyLogging
import securityproject.api.routes.Routes

import scala.util.{Failure, Success}

object Boot extends App with LazyLogging {

  implicit val system: ActorSystem = AppActorSystem.system
  implicit val executionContext: ExecutionContext = AppActorSystem.executionContext

  Http().newServerAt("0.0.0.0", 1070).bind(Routes.routes).onComplete {
    case Success(_) => logger.info("HTTP binding complete!")
    case Failure(e) => logger.error("HTTP binding failed", e)
  }

}