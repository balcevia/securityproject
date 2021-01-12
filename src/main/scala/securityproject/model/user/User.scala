package securityproject.model.user

import securityproject.model.Identifiable

/**
  * Created by Alfred on 12.01.2021.
  * */

case class User(id: Option[Long],
                username: String,
                password: Array[Byte],
                salt: Array[Byte],
                loginCount: Int
               ) extends Identifiable[Long]