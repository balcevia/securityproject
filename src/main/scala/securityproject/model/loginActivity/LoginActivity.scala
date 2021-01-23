package securityproject.model.loginActivity

import java.time.LocalDateTime

import securityproject.model.Identifiable

case class LoginActivity(id: Option[Int],
                         userId: Long,
                         ipAddress: String,
                         timestamp: LocalDateTime) extends Identifiable[Int, LoginActivity] {
  override def withId(id: Option[Int]): LoginActivity = copy(id = id)
}
