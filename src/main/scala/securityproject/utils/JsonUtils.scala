package securityproject.utils

/**
  * Created by Alfred on 12.01.2021.
  */
import org.json4s._
import org.json4s.native.Serialization.{read, write, writePretty}

object JsonUtils {

  val customSerializers = Seq(
    new ArrayBytesSerializer,
    new LocalDateTimeSerializer
  )

  implicit val formats: Formats = DefaultFormats ++ customSerializers

  def format[T <: AnyRef](t: T): String = formatPretty(t) //fixme

  def formatPretty[T <: AnyRef]: T => String = (t: T) => writePretty(t)

  def formatCompressed[T <:AnyRef](t: T): String = write(t)

  def parse[T](body: String)(implicit manifest: Manifest[T]): T = read(body)

  def parse[T](jValue: JValue)(implicit manifest: Manifest[T]): T = jValue.extract[T]

}