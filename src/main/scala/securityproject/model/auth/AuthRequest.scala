package securityproject.model.auth

/**
  * Created by Alfred on 12.01.2021.
  */
case class AuthRequest(email: Option[String], password: Option[Array[Byte]])
