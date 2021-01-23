package securityproject.model

import akka.stream.alpakka.slick.scaladsl.Slick
import akka.stream.scaladsl.{Keep, Sink, Source}
import com.typesafe.scalalogging.LazyLogging
import slick.jdbc.MySQLProfile.ProfileAction
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.Future

abstract class RawDAO[E, T <: Table[E]] extends LazyLogging {

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

  protected def insertOrUpdateAction(entity: E): ProfileAction[Int, NoStream, Effect.Write] = tableQuery.insertOrUpdate(entity)

  protected def insertAction(entity: E): ProfileAction[Int, NoStream, Effect.Write] = tableQuery += entity

  def insertOrUpdate(entity: E): Future[Int] = Source.single(entity).runWith(Slick.sink(insertOrUpdateAction)).map(_ => 1)

  def insert(entity: E): Future[Int] = Source.single(entity).runWith(Slick.sink(insertAction)).map(_ => 1)

  def deleteByQueryAction(query: T => Rep[Option[Boolean]]): DBIO[Int] = getAllQuery.filter(query).delete

  def deleteByQuery(query: T => Rep[Option[Boolean]]): Future[Int] = Source.single(query).via(Slick.flow(deleteByQueryAction)).runWith(Sink.head)


}
