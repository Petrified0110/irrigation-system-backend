package api

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.headers.{Authorization, OAuth2BearerToken}
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, Uri}
import domain.GPSData

import java.time.{OffsetDateTime, ZoneOffset}
import scala.concurrent.Future

object ApiRoutes {

  val baseEndpointNrf = "https://api.nrfcloud.com/v1"
  val baseEndpointForecast = "http://api.weatherapi.com/v1/forecast.json"

  def pollMessages(deviceId: String, nrfToken: String, start: Option[OffsetDateTime], end: Option[OffsetDateTime])(
    implicit as: ActorSystem
  ): Future[HttpResponse] = {
    val inclusiveStart = start.getOrElse(OffsetDateTime.now()).atZoneSameInstant(ZoneOffset.UTC)
    val exclusiveEnd = end.getOrElse(OffsetDateTime.now()).atZoneSameInstant(ZoneOffset.UTC)

    singleRequest(
      HttpRequest(
        uri = Uri(
          s"$baseEndpointNrf/messages?deviceId=$deviceId&inclusiveStart=$inclusiveStart&exclusiveEnd=$exclusiveEnd"),
        headers = Seq(Authorization(OAuth2BearerToken(nrfToken)))
      ))
  }

  def getNrfDevice(deviceId: String, nrfToken: String)(
    implicit as: ActorSystem
  ): Future[HttpResponse] = {

    singleRequest(
      HttpRequest(
        uri = Uri(s"$baseEndpointNrf/devices/$deviceId"),
        headers = Seq(Authorization(OAuth2BearerToken(nrfToken)))
      ))
  }

  def getPredictionData(
    gpsData: GPSData,
    weatherApiToken: String
  )(
    implicit as: ActorSystem
  ): Future[HttpResponse] = {

    singleRequest(
      HttpRequest(
        uri =
          Uri(s"$baseEndpointForecast?key=$weatherApiToken&q=${gpsData.latitude},${gpsData.longitude}&aqi=yes&days=10"),
      ))

  }

  private def singleRequest(httpRequest: HttpRequest)(
    implicit as: ActorSystem
  ): Future[HttpResponse] =
    Http().singleRequest(httpRequest)
}
