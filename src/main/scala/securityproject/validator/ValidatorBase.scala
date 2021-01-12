package securityproject.validator

/**
  * Created by Alfred on 12.01.2021.
  */
import scala.concurrent.Future
import scala.util.Try

trait ValidatorBase {
  protected def validateRequired[T](value: Option[T], message: => String): Future[T] = Future.fromTry(Try {
    value.getOrElse(throw new Exception(message))
  })
}