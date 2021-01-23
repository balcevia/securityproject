package securityproject.model.file

case class FileUploadInfo(
                           allowedTo: Option[List[Long]],
                           isPrivate: Boolean = false,
                           shouldEncrypt: Boolean = false,
                           key: Option[Array[Byte]]
                         )
