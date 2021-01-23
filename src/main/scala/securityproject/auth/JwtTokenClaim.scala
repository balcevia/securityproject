package securityproject.auth

/**
  * Created by Alfred on 12.01.2021.
  */
import java.time.LocalDateTime

case class JwtTokenClaim(userId: Long, email: String, expiration: LocalDateTime)