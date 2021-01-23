package securityproject

/**
  * Created by Alfred on 17.01.2021.
  */

import java.io.InputStream
import java.security.{KeyStore, SecureRandom}

import akka.http.scaladsl.{ConnectionContext, HttpsConnectionContext}
import javax.net.ssl.{KeyManagerFactory, SSLContext, TrustManagerFactory}

object HttpConfig {

  private val password: Array[Char] = AppConfig.getString("ssl.password").toCharArray

  private val ks: KeyStore = KeyStore.getInstance("PKCS12")
  private val keystore: InputStream = getClass.getClassLoader.getResourceAsStream("cert/keyStore.p12")

  require(keystore != null, "Keystore required!")
  ks.load(keystore, password)

  private val keyManagerFactory: KeyManagerFactory = KeyManagerFactory.getInstance("SunX509")
  keyManagerFactory.init(ks, password)

  private val tmf: TrustManagerFactory = TrustManagerFactory.getInstance("SunX509")
  tmf.init(ks)

  private val sslContext: SSLContext = SSLContext.getInstance("TLS")
  sslContext.init(keyManagerFactory.getKeyManagers, tmf.getTrustManagers, new SecureRandom)

  val https: HttpsConnectionContext = ConnectionContext.httpsServer(sslContext)
}
