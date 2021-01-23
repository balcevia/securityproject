package securityproject.service
import java.io.File
import org.apache.commons.io.FileUtils
import securityproject.auth.PasswordHash
import securityproject.mappers.PersistedFileInfoMapper
import securityproject.model.file._
import securityproject.utils.CryptoUtils

import scala.concurrent.{ExecutionContext, Future}

trait FileService {

  protected val persistedFileInfoDAO: PersistedFileInfoDAO
  protected val fileUserAssociationDAO: FileUserAssociationDAO
  protected val persistedFileInfoMapper: PersistedFileInfoMapper

  def persistFile(fileName: String, filePath: String, key: Option[Array[Byte]], salt: Option[Array[Byte]], fileUploadInfo: FileUploadInfo, userId: Long)(implicit ec: ExecutionContext): Future[Unit] = {
    for {
      insertedFile <- persistedFileInfoDAO.insertWithAutoIncrement(PersistedFileInfo(None, userId, filePath, fileName, fileUploadInfo.isPrivate, key, salt))
      allowedTo = if (fileUploadInfo.isPrivate) fileUploadInfo.allowedTo.getOrElse(Nil) :+ userId else Nil
      fileUserAssociations = allowedTo.map(id => FileUserAssociation(id, insertedFile.id.get))
      _ <- Future.sequence(fileUserAssociations.map(fileUserAssociationDAO.insert))
    } yield ()
  }

  def saveFile(file: File, fileName: String, fileUploadInfo: FileUploadInfo, userId: Long)(implicit ec: ExecutionContext): Future[Unit] = {
    val fileInfo = if (fileUploadInfo.shouldEncrypt) saveEncryptedFile(file, fileName, fileUploadInfo.key.get) else saveFile(file, fileName)
    file.delete()
    persistFile(fileInfo._1, fileInfo._2, fileInfo._3, fileInfo._4, fileUploadInfo, userId)
  }

  private def saveEncryptedFile(file: File, fileName: String, keyPhrase: Array[Byte]): (String, String, Option[Array[Byte]], Option[Array[Byte]]) = {
    val filePath = "/home/" + System.currentTimeMillis() + fileName
    val outputFile = new File(filePath)

    val salt = PasswordHash.generateSalt()
    val key: Array[Byte] = PasswordHash.generateHash(keyPhrase, salt)

    CryptoUtils.encrypt(key, file, outputFile)
    (fileName, filePath, Some(key), Some(salt))
  }

  private def saveFile(file: File, fileName: String): (String, String, Option[Array[Byte]], Option[Array[Byte]]) = {
    val filePath = "/home/" + System.currentTimeMillis() + fileName
    FileUtils.copyFile(file, new File(filePath))
    (fileName, filePath, None, None)
  }

  def getFiles(userId: Long)(implicit ex: ExecutionContext): Future[Seq[PersistedFileInfoDTO]] = {
    for {
      associations <- fileUserAssociationDAO.getByUserId(userId)
      files <- persistedFileInfoDAO.getByIds(associations.map(_.fileId))
      mappedFiles <- persistedFileInfoMapper.toDTO(files)
    } yield mappedFiles
  }

  def getFileById(userId: Long, request: FileGetRequest)(implicit ec: ExecutionContext): Future[File] = {
    persistedFileInfoDAO.getByIdOrFail(request.id).flatMap {
      case file: PersistedFileInfo if !file.isPrivate => Future.successful(file)
      case file =>
        fileUserAssociationDAO.getByUserIdAndFileId(userId, file.id.get).flatMap {
          case Some(_) => Future.successful(file)
          case _ => Future.failed(new Exception("File Not Found"))
        }
    }.flatMap {
      case file if file.secretKey.isDefined =>
        if (request.key.isDefined) {
          val outFile = File.createTempFile("tmp", "")
          outFile.deleteOnExit()
          val key = PasswordHash.generateHash(request.key.get, file.salt.get)

          if (key.deep == file.secretKey.get.deep) {
            Future.successful(CryptoUtils.decrypt(file.secretKey.get, new File(file.filePath), outFile))
          } else Future.failed(new Exception("Access Denied"))
        } else {
          Future.failed(new Exception("Access Denied"))
        }
      case file => Future.successful(new File(file.filePath))
    }
  }
}

object FileService extends FileService {
  override protected val persistedFileInfoDAO: PersistedFileInfoDAO = PersistedFileInfoDAO
  override protected val fileUserAssociationDAO: FileUserAssociationDAO = FileUserAssociationDAO
  override protected val persistedFileInfoMapper: PersistedFileInfoMapper = PersistedFileInfoMapper
}