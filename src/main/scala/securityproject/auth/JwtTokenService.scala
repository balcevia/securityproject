package securityproject.auth

import org.json4s.Extraction
import org.json4s.JsonAST.JObject
import pdi.jwt.{JwtAlgorithm, JwtJson4s}
import pdi.jwt.algorithms.JwtHmacAlgorithm
import securityproject.AppConfig
import securityproject.utils.JsonUtils

import scala.util.Try

trait JwtTokenService {
  import JwtTokenService._

  def generateToken(claim: JwtTokenClaim): String = {
    import JsonUtils._
    val json = Extraction.decompose(claim).asInstanceOf[JObject]
    JwtJson4s.encode(json, secretKey, algorithm)
  }

  def decode(token: String): Try[JwtTokenClaim] = {
    for {
      claim <- JwtJson4s.decodeJson(token, secretKey, Seq(algorithm))
    } yield JsonUtils.parse[JwtTokenClaim](claim)
  }

}

object JwtTokenService extends JwtTokenService {
  protected val algorithm: JwtHmacAlgorithm = JwtAlgorithm.fromString(AppConfig.getString("auth.jwt.algorithm")).asInstanceOf[JwtHmacAlgorithm]
  protected val secretKey: String = AppConfig.getString("auth.jwt.secret")
}