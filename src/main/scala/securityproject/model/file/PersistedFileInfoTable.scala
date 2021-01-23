package securityproject.model.file

import securityproject.model.DB.session.profile.api._
import securityproject.model.TableWithId

class PersistedFileInfoTable(tag: Tag) extends TableWithId[PersistedFileInfo, Int](tag, "PERSISTED_FILE_INFO") {

  def id = column[Option[Int]]("ID", O.PrimaryKey, O.AutoInc)

  def userId = column[Long]("USER_ID")

  def filePath = column[String]("FILE_PATH")

  def fileName = column[String]("FILE_NAME")

  def isPrivate = column[Boolean]("IS_PRIVATE")

  def secretKey = column[Option[Array[Byte]]]("KEY")

  def salt = column[Option[Array[Byte]]]("SALT")

  def * = (id, userId, filePath, fileName, isPrivate, secretKey, salt) <> (PersistedFileInfo.tupled, PersistedFileInfo.unapply)

}
