package securityproject.service

import java.time.LocalDateTime

import securityproject.model.loginActivity.{LoginActivity, LoginActivityDAO}

import scala.concurrent.{ExecutionContext, Future}

trait LoginActivityService {

  protected val loginActivityDAO: LoginActivityDAO

  def addActivity(userId: Long, ipAddress: String)(implicit ec: ExecutionContext): Future[Int] = {
    loginActivityDAO.getByUserIdAndIp(userId, ipAddress).flatMap {
      case Some(activity) =>
        loginActivityDAO.insertOrUpdate(activity.copy(timestamp = LocalDateTime.now()))
      case None =>
        loginActivityDAO.insertOrUpdate(LoginActivity(None, userId, ipAddress, LocalDateTime.now()))
    }
  }

  def getActivityForUser(id: Long): Future[Seq[LoginActivity]] ={
    loginActivityDAO.getByUserId(id)
  }
}


object LoginActivityService extends LoginActivityService {
  override protected val loginActivityDAO: LoginActivityDAO = LoginActivityDAO
}
