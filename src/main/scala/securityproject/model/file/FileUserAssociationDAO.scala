package securityproject.model.file

import securityproject.model.RawDAO
import securityproject.model.DB.session.profile.api._

import scala.concurrent.{ExecutionContext, Future}

trait FileUserAssociationDAO extends RawDAO[FileUserAssociation, FileUserAssociationTable] {

  def getByUserId(userId: Long): Future[Seq[FileUserAssociation]] = getByQuery(_.userId === userId)

  def getByUserIdAndFileId(userId: Long, id: Int)(implicit ec: ExecutionContext): Future[Option[FileUserAssociation]] =
    getByQuery(f => f.userId === userId && f.fileId === id).map(_.headOption)
}

object FileUserAssociationDAO extends FileUserAssociationDAO {
  override protected val tableQuery: TableQuery[FileUserAssociationTable] = TableQuery[FileUserAssociationTable]
}
