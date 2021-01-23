package securityproject.utils

/**
  * Created by Alfred on 17.01.2021.
  */
import org.json4s.{Formats, JString, JValue, Serializer, TypeInfo}

import scala.collection.mutable
import scala.reflect.api
import scala.reflect.api.{TypeCreator, Universe}
import securityproject.model.CustomEnum
import scala.reflect.runtime.universe._

class EnumSerializer extends Serializer[CustomEnum[_]] {

  def matchesType(typeInfo: TypeInfo): Boolean = classOf[CustomEnum[_]].isAssignableFrom(typeInfo.clazz)

  private def cache = mutable.Map.empty[Class[_], TypeTag[_]]

  private def toTypeTag[A <: CustomEnum[A]](c: Class[_]): TypeTag[A] = {
    cache
      .getOrElseUpdate(c, {
        val mirror = runtimeMirror(c.getClassLoader)
        val sym = mirror.staticClass(c.getName)
        val tpe = sym.selfType
        TypeTag(mirror, new TypeCreator {
          def apply[U <: Universe with Singleton](m: api.Mirror[U]) =
            if (m eq mirror) tpe.asInstanceOf[U#Type]
            else throw new IllegalArgumentException(s"Type tag defined in $mirror cannot be migrated to other mirrors.")
        })
      })
      .asInstanceOf[TypeTag[A]]
  }

  override def deserialize(implicit format: Formats): PartialFunction[(TypeInfo, JValue), CustomEnum[_]] = {
    case (ti@TypeInfo(clazz, _), JString(value)) if matchesType(ti) =>
      CustomEnum.fromString(value)(toTypeTag(clazz))
  }

  override def serialize(implicit format: Formats): PartialFunction[Any, JValue] = {
    case e: CustomEnum[_] => JString(e.value)
  }
}

