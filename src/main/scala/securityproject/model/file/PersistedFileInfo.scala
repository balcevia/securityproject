package securityproject.model.file

import securityproject.model.Identifiable

case class PersistedFileInfo(
                              id: Option[Int],
                              userId: Long,
                              filePath: String,
                              fileName: String,
                              isPrivate: Boolean,
                              secretKey: Option[Array[Byte]],
                              salt: Option[Array[Byte]]
                            ) extends Identifiable[Int, PersistedFileInfo] {
  override def withId(id: Option[Int]): PersistedFileInfo = copy(id = id)
}
