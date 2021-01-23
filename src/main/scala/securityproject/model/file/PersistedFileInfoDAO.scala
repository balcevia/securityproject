package securityproject.model.file

import securityproject.model.DAO
import slick.lifted.TableQuery
import securityproject.model.DB.session.profile.api._

import scala.concurrent.Future

trait PersistedFileInfoDAO extends DAO[PersistedFileInfo, Int, PersistedFileInfoTable] {
  def getByIds(ids: Seq[Int]): Future[Seq[PersistedFileInfo]] = getByQuery(f => (f.id inSet ids) || !f.isPrivate)

}

object PersistedFileInfoDAO extends PersistedFileInfoDAO {
  override protected val tableQuery: TableQuery[PersistedFileInfoTable] = TableQuery[PersistedFileInfoTable]
}
