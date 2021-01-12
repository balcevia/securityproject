package securityproject.api

/**
  * Created by Alfred on 12.01.2021.
  */
sealed trait ResponseContainer {
  val timestamp: String
}

case class OkResponse(response: Any,
                      timestamp: String) extends ResponseContainer

case class ErrorResponse(error: String,
                         timestamp: String) extends ResponseContainer