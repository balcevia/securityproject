package securityproject.model.loginActivity

import securityproject.model.DAO
import slick.lifted.TableQuery
import securityproject.model.DB.session.profile.api._

import scala.concurrent.{ExecutionContext, Future}

trait LoginActivityDAO extends DAO[LoginActivity, Int, LoginActivityTable] {

  def getByUserIdAndIp(userId: Long, ip: String)(implicit ec: ExecutionContext): Future[Option[LoginActivity]] =
    getByQuery(a => a.userId === userId && a.ipAddress === ip).map(_.headOption)

  def getByUserId(id: Long): Future[Seq[LoginActivity]] = getByQuery(_.userId === id)
}

object LoginActivityDAO extends LoginActivityDAO {
  override protected val tableQuery: TableQuery[LoginActivityTable] = TableQuery[LoginActivityTable]
}
