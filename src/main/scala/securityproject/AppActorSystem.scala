package securityproject

/**
  * Created by Alfred on 12.01.2021.
  */
import akka.actor.ActorSystem

import scala.concurrent.ExecutionContext

object AppActorSystem {
  implicit val system: ActorSystem = ActorSystem()
  implicit val executionContext: ExecutionContext = system.dispatcher
}