package securityproject.model.file

case class PersistedFileInfoDTO(
                                 id: Option[Int],
                                 owner: String,
                                 fileName: String,
                                 isPrivate: Boolean,
                                 shouldEncrypt: Boolean
                               )
