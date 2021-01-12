package securityproject.model.user
import securityproject.model.DB.session.profile.api._
import securityproject.model.TableWithId

/**
  * Created by Alfred on 12.01.2021.
  */
class UserTable(tag: Tag) extends TableWithId[User, Long](tag, "USERS") {

  def id = column[Option[Long]]("ID", O.PrimaryKey)

  def username = column[String]("USERNAME")

  def password = column[Array[Byte]]("PASSWORD")

  def salt = column[Array[Byte]]("SALT")

  def loginCount = column[Int]("INVALID_LOGIN_COUNT")

  def * = (id, username, password, salt, loginCount) <> (User.tupled, User.unapply)
}