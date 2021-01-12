package securityproject.model.user

/**
  * Created by Alfred on 12.01.2021.
  */
case class UserPostRequest(username: Option[String], password: Option[Array[Byte]])