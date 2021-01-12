package securityproject.auth

/**
  * Created by Alfred on 12.01.2021.
  */
import java.security.{MessageDigest, SecureRandom}

trait PasswordHash {
  def generateHash(password: Array[Byte], salt: Array[Byte]): Array[Byte]

  def generateSalt(): Array[Byte]
}

object PasswordHash extends PasswordHash {
  private lazy val md = MessageDigest.getInstance("SHA-512")
  private lazy val sr = SecureRandom.getInstance("SHA1PRNG", "SUN")

  def generateHash(password: Array[Byte], salt: Array[Byte]): Array[Byte] = {
    md.update(salt)
    val bytes = md.digest(password)
    HexConverter.toHex(bytes)
  }

  def generateSalt(): Array[Byte] = {
    val salt = new Array[Byte](16)
    sr.nextBytes(salt)
    HexConverter.toHex(salt)
  }
}