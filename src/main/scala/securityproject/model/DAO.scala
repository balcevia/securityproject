package securityproject.model

/**
  * Created by Alfred on 12.01.2021.
  */
import slick.ast.BaseTypedType
import akka.stream.alpakka.slick.scaladsl._
import akka.stream.scaladsl._
import com.typesafe.scalalogging.LazyLogging
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.Future

abstract class DAO[E <: Identifiable[ID, E], ID: BaseTypedType, T <: TableWithId[E, ID]] extends RawDAO[E, T] with LazyLogging {

  import DB._
  import securityproject.AppActorSystem._

  protected def tableQuery: TableQuery[T]

  def getById(id: ID): Future[Option[E]] = getByQuery(_.id === id).flatMap { list =>
    if (list.size > 1) {
      val errorMessage = "More than one record found for getById query"
      logger.error(errorMessage)
      Future.failed(new Exception(errorMessage))
    } else {
      Future.successful(list.headOption)
    }
  }

  def getByIdOrFail(id: ID): Future[E] = getById(id).flatMap {
    case None =>
      val tableName = tableQuery.baseTableRow.tableName
      val message = s"Record with id $id does not exist in table $tableName"
      logger.error(message)
      Future.failed(new Exception(message))
    case Some(entity) => Future.successful(entity)
  }

  def deleteByIdAction(id: ID): DBIO[Int] = deleteByQueryAction(t => t.id === id)

  def deleteById(id: ID): Future[Int] = Source.single(id).runWith(Slick.sink(id => deleteByIdAction(id))).map(_ => 1)

  private def insertWithAutoIncrementAction(t: E): DBIO[E] = (tableQuery returning tableQuery.map(_.id) into ((item, id) => item.withId(id))) += t

  def insertWithAutoIncrement(entity: E): Future[E] = Source.single(entity)
    .via(Slick.flowWithPassThrough(insertWithAutoIncrementAction)).runWith(Sink.head)
}