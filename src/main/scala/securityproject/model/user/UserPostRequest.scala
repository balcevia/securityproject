package securityproject.model.user

/**
  * Created by Alfred on 12.01.2021.
  */
case class UserPostRequest(email: Option[String], password: Option[Array[Byte]], confirmPassword: Option[Array[Byte]])