package securityproject.validator

import scala.concurrent.{ExecutionContext, Future}

trait FileValidator extends ValidatorBase {

  def validateAllowedToList(allowTo: String)(implicit ec: ExecutionContext): Future[List[Long]] = allowTo match {
    case "none" => Future.successful(Nil)
    case str =>
      val splitedStr = str.split(",")
      val isListValid = splitedStr.forall(_.forall(_.isDigit))
      if (isListValid) {
        Future.successful(splitedStr.map(_.toLong).toList)
      } else Future.failed(new Exception("Unable to parse allowTo query parameter"))
  }
}

object FileValidator extends FileValidator