package securityproject.model

/**
  * Created by Alfred on 12.01.2021.
  */
import akka.Done
import slick.ast.BaseTypedType
import akka.stream.alpakka.slick.scaladsl._
import akka.stream.scaladsl._
import com.typesafe.scalalogging.LazyLogging
import slick.jdbc.MySQLProfile.api._
import slick.jdbc.MySQLProfile._

import scala.concurrent.Future

abstract class DAO[E <: Identifiable[ID], ID: BaseTypedType, T <: TableWithId[E, ID]] extends LazyLogging {

  import DB._
  import securityproject.AppActorSystem._

  protected def tableQuery: TableQuery[T]

  def getAllQuery: Query[T, E, Seq] = tableQuery

  def getByQueryAction(query: T => Rep[Option[Boolean]]): Query[T, E, Seq] = getAllQuery.filter(query)

  def getAll: Future[Seq[E]] = Slick.source(getAllQuery.result).toMat(Sink.seq)(Keep.right).run()

  def getByQuery(query: T => Rep[Option[Boolean]]): Future[Seq[E]] =
    Slick.source(getByQueryAction(query).result)
      .toMat(Sink.seq)(Keep.right)
      .run()

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

  protected def insertOrUpdateAction(entity: E): ProfileAction[Int, NoStream, Effect.Write] = tableQuery.insertOrUpdate(entity)

  def insertOrUpdate(entity: E): Future[Int] = Source.single(entity).runWith(Slick.sink(insertOrUpdateAction)).map(_ => 1)

  def deleteByQueryAction(query: T => Rep[Option[Boolean]]): ProfileAction[Int, NoStream, Effect.Write] = getAllQuery.filter(query).delete

  def deleteByQuery(query: T => Rep[Option[Boolean]]): Future[Done] = Source.single(query).runWith(Slick.sink(deleteByQueryAction))

  def deleteByIdAction(id: ID): ProfileAction[Int, NoStream, Effect.Write] = deleteByQueryAction(t => t.id === id)

  def deleteById(id: ID): Future[Int] = Source.single(id).runWith(Slick.sink(id => deleteByIdAction(id))).map(_ => 1)
}