package securityproject.model

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import slick.ast.BaseTypedType
import slick.jdbc.JdbcType
import slick.jdbc.MySQLProfile.api._
import securityproject.utils.JsonUtils

object ColumnMappers {
  implicit def enumColumnMapper[T <: CustomEnum[T]](implicit m: Manifest[T]): JdbcType[T] with BaseTypedType[T] = CustomEnum.mappedEnumColumnType

  implicit def listMapper[T](implicit m: Manifest[T]): JdbcType[List[T]] with BaseTypedType[List[T]] = JsonUtils.mappedJsonColumnType[List[T]]

  val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

  implicit val dateTimeColumnType: BaseColumnType[LocalDateTime] = MappedColumnType.base[LocalDateTime, String] (
    l => l.format(dateTimeFormatter),
    d => LocalDateTime.parse(d, dateTimeFormatter)
  )
}
