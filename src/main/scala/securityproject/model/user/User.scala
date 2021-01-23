package securityproject.model.user

import securityproject.model.Identifiable

/**
  * Created by Alfred on 12.01.2021.
  * */

case class User(id: Option[Long],
                email: String,
                password: Array[Byte],
                salt: Array[Byte],
                invalidLoginCount: Int,
               ) extends Identifiable[Long, User] {
  override def withId(id: Option[Long]): User = copy(id = id)
}