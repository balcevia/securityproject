package securityproject.mappers

import securityproject.model.file.{PersistedFileInfo, PersistedFileInfoDTO}
import securityproject.model.user.User
import securityproject.service.UserService

import scala.concurrent.{ExecutionContext, Future}

trait PersistedFileInfoMapper {

  protected val userService: UserService

  def toDTO(files: Seq[PersistedFileInfo])(implicit ec: ExecutionContext): Future[Seq[PersistedFileInfoDTO]] = {
    for {
      usersMap <- userService.getByIds(files.map(_.userId).distinct).map(_.map(u => (u.id.get, u)).toMap)
      mappedFiles = files.map(f => toDTO(f, usersMap(f.userId)))
    } yield mappedFiles
  }

  def toDTO(file:PersistedFileInfo, user: User): PersistedFileInfoDTO = PersistedFileInfoDTO(file.id, user.email, file.fileName, file.isPrivate, file.secretKey.isDefined)

}

object PersistedFileInfoMapper extends PersistedFileInfoMapper {
  override protected val userService: UserService = UserService
}
