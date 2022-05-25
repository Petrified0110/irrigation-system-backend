package processing

import akka.actor.ActorSystem
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.Materializer
import api.ApiRoutes
import domain._
import io.circe.generic.auto._
import io.circe.parser.decode

import scala.concurrent.{ExecutionContext, Future}

object ForecastDataProcessor {

  def processAndRetrieve(location: GPSData)(
    implicit m: Materializer,
    ec: ExecutionContext,
    as: ActorSystem
  ): Future[Either[String, ForecastForLocation]] = {
    val response = ApiRoutes.getPredictionData(location)

    response
      .flatMap(Unmarshal(_).to[String])
      .collect { stringData =>
        decode[ForecastForLocation](stringData) match {
          case Right(forecast) =>
            Right(forecast)
          case Left(a) => Left(a + "Retrieved forecast data couldn't be decoded")
        }
      }
  }

  def toForecastAndLocation(forecast: ForecastForLocation) = {
    ForecastAndLocation(
      forecast = ForecastForFrontend(
        forecast = forecast.forecast.forecastday
          .map(forecastForDate =>
            ForecastForDay(
              date = forecastForDate.date.toString,
              description = forecastForDate.day.condition.text,
              icon = "",
              temperature =
                Temperature(None, forecastForDate.day.mintemp_c.toString, forecastForDate.day.maxtemp_c.toString),
              wind = forecastForDate.day.maxwind_kph.toString,
              humidity = forecastForDate.day.avghumidity.toInt
          ))
          .toList,
        current = ForecastForDay(
          date = forecast.location.localtime,
          description = forecast.current.condition.text,
          icon = "",
          temperature = Temperature(Some(forecast.current.temp_c.toString), "", ""),
          wind = forecast.current.wind_mph.toString,
          humidity = forecast.current.humidity
        )
      ),
      location = s"${forecast.location.name} ${forecast.location.region}, ${forecast.location.country}"
    )
  }

}
