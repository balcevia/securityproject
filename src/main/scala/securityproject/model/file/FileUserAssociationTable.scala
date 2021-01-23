package securityproject.model.file

import slick.lifted.Tag
import slick.jdbc.MySQLProfile.api._

class FileUserAssociationTable(tag: Tag) extends Table[FileUserAssociation](tag, "FILE_USER_ASSOCIATION") {

  def userId = column[Long]("USER_ID")

  def fileId = column[Int]("FILE_ID")

  def * = (userId, fileId) <> (FileUserAssociation.tupled, FileUserAssociation.unapply)

}
