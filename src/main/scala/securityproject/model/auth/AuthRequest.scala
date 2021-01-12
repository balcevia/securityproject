package securityproject.model.auth

/**
  * Created by Alfred on 12.01.2021.
  */
case class AuthRequest(username: Option[String], password: Option[Array[Byte]])
