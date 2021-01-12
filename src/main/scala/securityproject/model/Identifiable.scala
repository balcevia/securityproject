package securityproject.model

/**
  * Created by Alfred on 12.01.2021.
  */
trait Identifiable[ID] {
  def id: Option[ID]
}