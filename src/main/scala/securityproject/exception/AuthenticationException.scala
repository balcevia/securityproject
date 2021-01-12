package securityproject.exception

/**
  * Created by Alfred on 12.01.2021.
  */
case class AuthenticationException(message: String = "",
                                   cause: Option[Throwable] = None
                                  ) extends Exception(message, cause.orNull)