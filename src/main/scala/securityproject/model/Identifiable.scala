package securityproject.model

/**
  * Created by Alfred on 12.01.2021.
  */
trait Identifiable[ID, E] {
  def id: Option[ID]
  def withId(id: Option[ID]): E
}