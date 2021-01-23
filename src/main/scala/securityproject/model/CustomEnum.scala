package securityproject.model

import slick.ast.BaseTypedType
import slick.jdbc.JdbcType

import scala.reflect.runtime.universe._

trait CustomEnum[E] {
  val value: String

  def asSuperType: E = this.asInstanceOf[E]

  override def toString: String = value
}

trait CaseInsensitiveEnum[E] extends CustomEnum[E]

object CustomEnum {

  private var cache = Map.empty[TypeTag[_], Set[_]]

  private def readValues[T <: CustomEnum[T]](tt: TypeTag[T]): Set[T] = {
    val tpe = typeOf[T](tt)
    val clazz = tpe.typeSymbol.asClass
    val mirror = reflect.runtime.currentMirror

    clazz.knownDirectSubclasses
      .filter(_.isModuleClass)
      .map { c =>
        val module = c.owner.typeSignature.member(c.name.toTermName)
        val instance = mirror.reflectModule(module.asModule).instance
        instance.asInstanceOf[T]
      }
  }

  def values[T <: CustomEnum[T]](implicit tt: TypeTag[T]): Set[T] = {
    cache.get(tt) match {
      case Some(set) =>set.asInstanceOf[Set[T]]
      case None =>
        val set = readValues(tt)
        cache = cache + (tt -> set)
        set
    }
  }

  def fromStringSafe[T <: CustomEnum[T]](value: String)(implicit tt: TypeTag[T]): Option[T] = {
    val caseInsensitive = typeOf[T](tt).baseClasses.contains(typeOf[CaseInsensitiveEnum[T]].typeSymbol)
    val predicate =
      if(caseInsensitive) (v: T) => v.value.toLowerCase == value.toLowerCase
      else (v: T) => v.value == value

    values(tt).find(predicate)
  }

  def fromString[T <: CustomEnum[T]](value: String)(implicit tt: TypeTag[T]): T = {
    fromStringSafe(value).getOrElse({
      val tpe = typeOf[T](tt)
      val clazz = tpe.typeSymbol.asClass
      throw new RuntimeException(s"Invalid enum value for type: ${clazz.name} value: " + value)
    })
  }

  def mappedEnumColumnType[T <: CustomEnum[T]](implicit manifest: Manifest[T]): JdbcType[T] with BaseTypedType[T] = {
    import slick.jdbc.MySQLProfile.api._
    MappedColumnType.base[T, String](enum => enum.value, CustomEnum.fromString[T])
  }
}
