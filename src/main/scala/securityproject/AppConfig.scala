package securityproject

/**
  * Created by Alfred on 12.01.2021.
  */
import com.typesafe.config.ConfigFactory
import collection.JavaConverters._

object AppConfig {

  private lazy val config = ConfigFactory.parseResources("application.conf")

  def getString(key: String): String = config.getString(key)

  def getBoolean(key: String): Boolean = config.getBoolean(key)

  def getInt(key: String): Int = config.getInt(key)

  def getDouble(key: String): Double = config.getDouble(key)

  def getStringList(key: String): List[String] = config.getStringList(key).asScala.toList
}