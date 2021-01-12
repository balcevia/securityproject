package securityproject.service

import securityproject.auth.PasswordHash
import securityproject.model.user.{User, UserDAO, UserPostRequest}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by Alfred on 12.01.2021.
  */
trait UserService {

  protected val userDAO: UserDAO
  protected val passwordHash: PasswordHash

  def createNewUser(request: UserPostRequest)(implicit ec: ExecutionContext): Future[Unit] = {
    val salt = passwordHash.generateSalt()
    val hashedPassword = passwordHash.generateHash(request.password.get, salt)
    val user = User(None, request.username.get, hashedPassword, salt, 0)
    userDAO.insertOrUpdate(user).map(_ => ())
  }
}

object UserService extends UserService {
  override protected val userDAO: UserDAO = UserDAO
  override protected val passwordHash: PasswordHash = PasswordHash
}