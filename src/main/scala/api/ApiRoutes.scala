package api

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.headers.{Authorization, OAuth2BearerToken}
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, Uri}

import java.time.{OffsetDateTime, ZoneOffset}
import scala.concurrent.Future

object ApiRoutes {

  val baseEndpoint = "https://api.nrfcloud.com/v1"

  def pollMessages(deviceId: String, start: Option[OffsetDateTime], end: Option[OffsetDateTime])(
    implicit as: ActorSystem): Future[HttpResponse] = {
    val inclusiveStart = start.getOrElse(OffsetDateTime.now()).atZoneSameInstant(ZoneOffset.UTC)
    val exclusiveEnd = end.getOrElse(OffsetDateTime.now()).atZoneSameInstant(ZoneOffset.UTC)

    Http().singleRequest(
      HttpRequest(
        uri = Uri(
          s"$baseEndpoint/messages?deviceId=$deviceId&inclusiveStart=$inclusiveStart&exclusiveEnd=$exclusiveEnd"),
        headers = Seq(Authorization(OAuth2BearerToken("b9c7ed80b99bf0d221d58800c8d1b130b6103423")))
      ))
  }
}
