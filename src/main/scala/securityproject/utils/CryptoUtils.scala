package securityproject.utils

import java.io.{File, FileInputStream, FileOutputStream}
import java.security.Key

import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

trait CryptoUtils {

  protected val algorithm = "AES"
  protected val transformation = "AES"

  def encrypt(key: Array[Byte], inputFile: File, outputFile: File): File = {
    doCrypto(Cipher.ENCRYPT_MODE, key, inputFile, outputFile)
  }

  def decrypt(key: Array[Byte], inputFile: File, outputFile: File): File = {
    doCrypto(Cipher.DECRYPT_MODE, key, inputFile, outputFile)
  }

  private def doCrypto(cipherMode: Int, key: Array[Byte], inputFile: File, outputFile: File): File = {
    try {
      val secretKey: Key = new SecretKeySpec(key, algorithm)
      val cipher: Cipher = Cipher.getInstance(transformation)
      cipher.init(cipherMode, secretKey)

      val inputStream: FileInputStream = new FileInputStream(inputFile)
      val inputBytes = new Array[Byte](inputFile.length().toInt)
      inputStream.read(inputBytes)

      val outputBytes: Array[Byte] = cipher.doFinal(inputBytes)

      val outputStream: FileOutputStream = new FileOutputStream(outputFile)
      outputStream.write(outputBytes)

      inputStream.close()
      outputStream.close()
      outputFile
    } catch {
      case e: Exception => throw e
    }
  }

}

object CryptoUtils extends CryptoUtils