package securityproject.api.routes

import java.io.File

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import securityproject.AppDirectives
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.directives.FileInfo
import securityproject.auth.AuthToken
import securityproject.model.file.{FileGetRequest, FileUploadInfo, PersistedFileInfoDTO}
import securityproject.service.FileService
import securityproject.utils.JsonUtils

import scala.concurrent.duration._
import scala.concurrent.Future
import scala.util.{Failure, Success}

object FileRoutes extends AppDirectives {

  def routes: Route =
    pathPrefix("file") {
      concat(
        post {
          withoutSizeLimit {
            authenticatedRequests { token =>
              toStrictEntity(50.seconds) {
                storeUploadedFile("file", info => new File(s"/home/${System.currentTimeMillis() + info.fileName}")) { (fileInfo, file) =>
                  formField("info") { str =>
                    completeFuture {
                      saveFile(fileInfo, file, str, token)
                    }
                  }
                }
              }
            }
          }
        },
        path("download") {
          post {
            entity(as[FileGetRequest]) { fileGetRequest =>
              authenticatedRequests { token =>
                onComplete(getFileById(token, fileGetRequest)) {
                  case Success(file) =>
                    complete(HttpEntity.fromFile(ContentTypes.`application/octet-stream`, file))
                  case Failure(ex) => throw ex
                }

              }
            }
          }
        },
        get {
          authenticatedRequests { token =>
            completeFuture {
              getFiles(token)
            }
          }
        }
      )
    }

  def getFiles(token: AuthToken): Future[Seq[PersistedFileInfoDTO]] = {
    FileService.getFiles(token.userId)
  }

  def getFileById(token: AuthToken, fileGetRequest: FileGetRequest): Future[File] = {
    FileService.getFileById(token.userId, fileGetRequest)
  }

  def saveFile(fileInfo: FileInfo, file: File, info: String, token: AuthToken): Future[Unit] = {
    val fileUploadInfo = JsonUtils.parse[FileUploadInfo](info)
    FileService.saveFile(file, fileInfo.fileName, fileUploadInfo, token.userId)
  }

}
