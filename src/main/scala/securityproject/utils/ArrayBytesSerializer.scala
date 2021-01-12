package securityproject.utils

/**
  * Created by Alfred on 12.01.2021.
  */
import org.json4s.CustomSerializer
import org.json4s.JsonAST.JString

class ArrayBytesSerializer extends CustomSerializer[Array[Byte]](_ => ({
  case json: JString => json.values.getBytes
}, {
  case w: Array[Byte] => JString(new String(w))
}))