package securityproject.auth

/**
  * Created by Alfred on 12.01.2021.
  */
object HexConverter {
  def toHex(bytes: Array[Byte]): Array[Byte] = bytes.map("%02X" format _).mkString.getBytes
}