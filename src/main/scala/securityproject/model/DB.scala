package securityproject.model

/**
  * Created by Alfred on 12.01.2021.
  */
import akka.stream.alpakka.slick.scaladsl.SlickSession

object DB {
  import securityproject.AppActorSystem._

  implicit val session: SlickSession = SlickSession.forConfig("slick-mysql")
  system.registerOnTermination(session.close())
}