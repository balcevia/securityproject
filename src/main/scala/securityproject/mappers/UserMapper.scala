package securityproject.mappers

import securityproject.model.user.{User, UserDTO}

trait UserMapper {

  def toDTO(user: User): UserDTO = UserDTO(
    id = user.id.get,
    email = user.email
  )
}

object UserMapper extends UserMapper
