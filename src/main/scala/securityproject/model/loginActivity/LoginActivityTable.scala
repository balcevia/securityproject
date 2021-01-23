package securityproject.model.loginActivity

import securityproject.model.DB.session.profile.api._
import securityproject.model.TableWithId
import java.time.LocalDateTime

class LoginActivityTable(tag: Tag) extends TableWithId[LoginActivity, Int](tag, "LOGIN_ACTIVITY") {

  import securityproject.model.ColumnMappers._

  def id = column[Option[Int]]("ID", O.PrimaryKey)

  def userId = column[Long]("USER_ID")

  def ipAddress = column[String]("IP_ADDRESS")

  def timestamp = column[LocalDateTime]("TIMESTAMP")

  def * = (id, userId, ipAddress, timestamp) <> (LoginActivity.tupled, LoginActivity.unapply)
}
