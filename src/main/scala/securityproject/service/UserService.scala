package securityproject.service

import securityproject.auth.PasswordHash
import securityproject.mappers.UserMapper
import securityproject.model.user.{User, UserDAO, UserDTO, UserPostRequest}

import scala.concurrent.{ExecutionContext, Future}

trait UserService {

  protected val userDAO: UserDAO
  protected val passwordHash: PasswordHash
  protected val userMapper: UserMapper

  def createNewUser(request: UserPostRequest)(implicit ec: ExecutionContext): Future[Unit] = {
    val salt = passwordHash.generateSalt()
    val hashedPassword = passwordHash.generateHash(request.password.get, salt)
    val user = User(None, request.email.get, hashedPassword, salt, 0)
    userDAO.insertOrUpdate(user).map(_ => ())
  }

  def getByEmail(email: String)(implicit ec: ExecutionContext): Future[Option[User]] = userDAO.getByEmail(email)

  def insertOrUpdate(user: User)(implicit ec: ExecutionContext): Future[Int] = userDAO.insertOrUpdate(user)

  def getByIdOrFail(id: Long): Future[User] = userDAO.getByIdOrFail(id)

  def getByIds(ids: Seq[Long]): Future[Seq[User]] = userDAO.getByIds(ids)

  def getListOfUsers(userId: Long)(implicit ec: ExecutionContext): Future[Seq[UserDTO]] = userDAO.getAll.map(_.filter(!_.id.contains(userId))).map(_.map(userMapper.toDTO))
}

object UserService extends UserService {
  override protected val userDAO: UserDAO = UserDAO
  override protected val passwordHash: PasswordHash = PasswordHash
  override protected val userMapper: UserMapper = UserMapper
}