package api

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.headers.{Authorization, OAuth2BearerToken}
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, Uri}

import java.time.{OffsetDateTime, ZoneOffset}
import scala.concurrent.Future

object ApiRoutes {

  val baseEndpoint = "https://api.nrfcloud.com/v1"

  def pollMessages(deviceId: String, nrfToken: String, start: Option[OffsetDateTime], end: Option[OffsetDateTime])(
    implicit as: ActorSystem
  ): Future[HttpResponse] = {
    val inclusiveStart = start.getOrElse(OffsetDateTime.now()).atZoneSameInstant(ZoneOffset.UTC)
    val exclusiveEnd = end.getOrElse(OffsetDateTime.now()).atZoneSameInstant(ZoneOffset.UTC)

    singleRequest(
      HttpRequest(
        uri =
          Uri(s"$baseEndpoint/messages?deviceId=$deviceId&inclusiveStart=$inclusiveStart&exclusiveEnd=$exclusiveEnd"),
        headers = Seq(Authorization(OAuth2BearerToken(nrfToken)))
      ))
  }

  def getNrfDevice(deviceId: String, nrfToken: String)(
    implicit as: ActorSystem
  ): Future[HttpResponse] = {

    singleRequest(
      HttpRequest(
        uri = Uri(s"$baseEndpoint/devices/$deviceId"),
        headers = Seq(Authorization(OAuth2BearerToken(nrfToken)))
      ))
  }

  private def singleRequest(httpRequest: HttpRequest)(
    implicit as: ActorSystem
  ): Future[HttpResponse] =
    Http().singleRequest(httpRequest)
}
