package securityproject.utils

/**
  * Created by Alfred on 12.01.2021.
  */
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import org.json4s.CustomSerializer
import org.json4s.JsonAST.JString

class LocalDateTimeSerializer extends CustomSerializer[LocalDateTime](_ => ({
  case json: JString =>
    LocalDateTime.parse(json.values, LocalDateTimeSerializer.formatter)
}, {
  case dateTime: LocalDateTime =>
    JString(dateTime.format(LocalDateTimeSerializer.formatter))
}))

object LocalDateTimeSerializer {
  val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
}