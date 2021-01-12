package securityproject.model.user

/**
  * Created by Alfred on 12.01.2021.
  */
import securityproject.model.DAO
import slick.lifted.TableQuery

import securityproject.model.DB.session.profile.api._

import scala.concurrent.{ExecutionContext, Future}

trait UserDAO extends DAO[User, Long, UserTable] {
  def getByUsername(username: String)(implicit ec: ExecutionContext): Future[Option[User]] =
    getByQuery(_.username === username).map(_.headOption)
}

object UserDAO extends UserDAO {
  override protected def tableQuery = TableQuery[UserTable]
}
