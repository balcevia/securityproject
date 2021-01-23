package securityproject.auth

import java.security.SecureRandom
import java.security.spec.KeySpec

import javax.crypto.SecretKeyFactory
import javax.crypto.spec.{PBEKeySpec, SecretKeySpec}
import securityproject.AppConfig

trait PasswordHash {
  def generateHash(password: Array[Byte], salt: Array[Byte]): Array[Byte]

  def generateSalt(): Array[Byte]
}

object PasswordHash extends PasswordHash {
  private lazy val sr = SecureRandom.getInstance("SHA1PRNG", "SUN")
  private lazy val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
  private val hashNumber = AppConfig.getInt("auth.jwt.hashNumber")

  def generateHash(password: Array[Byte], salt: Array[Byte]): Array[Byte] = {
    val spec = new PBEKeySpec(password.map(_.toChar), salt, 65536, 128)
    var hash = factory.generateSecret(spec).getEncoded
    for (_ <- 1 to hashNumber) {
      val loopSpec = new PBEKeySpec(hash.map(_.toChar), salt, 65536, 128)
      hash = factory.generateSecret(loopSpec).getEncoded
    }
    HexConverter.toHex(hash)
  }

  def generateSalt(): Array[Byte] = {
    val salt = new Array[Byte](16)
    sr.nextBytes(salt)
    HexConverter.toHex(salt)
  }
}