package hm

import java.io.{File, InputStreamReader, Reader}

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.{GoogleAuthorizationCodeFlow, GoogleClientSecrets}
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.FileDataStoreFactory

import scala.collection.JavaConverters._

object Google {
  object Scopes {
    val EMAIL = "email"
    val PROFILE = "profile"
  }

  def newApplicationSecretsReader: Reader =
    new InputStreamReader(
      this.getClass.getResourceAsStream("/client_secrets.json")
    )
}

class Google {

  val httpTransport: NetHttpTransport = GoogleNetHttpTransport.newTrustedTransport()
  val jsonFactory: JsonFactory = JacksonFactory.getDefaultInstance
  val applicationSecrets: GoogleClientSecrets = GoogleClientSecrets.load(jsonFactory, Google.newApplicationSecretsReader)
  val authFlow: AuthorizationCodeFlow =
    new GoogleAuthorizationCodeFlow.Builder(
      httpTransport,
      jsonFactory,
      applicationSecrets,
      Seq(Google.Scopes.EMAIL).asJavaCollection
    )
    .setDataStoreFactory(new FileDataStoreFactory(new File("""d:\_discard\tmp\tokens""")))
    .setAccessType("offline")
    .build()
}
