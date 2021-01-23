package securityproject.model.user
import securityproject.model.DB.session.profile.api._
import securityproject.model.TableWithId

class UserTable(tag: Tag) extends TableWithId[User, Long](tag, "USERS") {

  def id = column[Option[Long]]("ID", O.PrimaryKey)

  def email = column[String]("EMAIL")

  def password = column[Array[Byte]]("PASSWORD")

  def salt = column[Array[Byte]]("SALT")

  def loginCount = column[Int]("INVALID_LOGIN_COUNT")

  def * = (id, email, password, salt, loginCount) <> (User.tupled, User.unapply)
}