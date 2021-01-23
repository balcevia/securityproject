package securityproject.utils

/**
  * Created by Alfred on 17.01.2021.
  */
import scala.math.{log10, pow}

object MathUtils {

  def log2: Double => Double = (x: Double) => log10(x) / log10(2.0)

  def entropy: (Double, Double) => Double = (x: Double, y: Double) => log2(pow(x, y))

}

