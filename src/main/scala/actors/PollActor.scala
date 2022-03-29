package actors

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.http.scaladsl.unmarshalling.Unmarshal
import api.ApiRoutes
import daos.{DeviceDao, PostgresDriver}
import domain.Device
import processing.SensorDataProcessor

import java.time.OffsetDateTime
import java.util.logging.Logger
import scala.util.{Failure, Success}

object PollActor {
  def props: Props = Props[PollActor]
}

class PollActor(
  serviceStartTime: OffsetDateTime,
  sensorDataProcessor: SensorDataProcessor,
  device: Device,
  deviceDao: DeviceDao)(
  implicit as: ActorSystem,
  db: PostgresDriver.backend.DatabaseDef
) extends Actor
    with ActorLogging {

  private val logger = Logger.getLogger(s"poll-actor-${device.deviceId}")
  import context.dispatcher

  def receive = {
    case "Poll" =>
      val from = device.lastPoll.getOrElse(serviceStartTime)
      val to = OffsetDateTime.now()

      logger.info(s"Calling nrf endpoint for device ${device.deviceId}")
      val response = ApiRoutes.pollMessages(device.deviceId, device.nrfToken, Some(from), Some(to))

      deviceDao.updateLastPoll(device.deviceId, to)
      val a = response
        .flatMap(Unmarshal(_).to[String])
        .collect { stringData =>
          sensorDataProcessor.processAndStore(stringData)
        }
        .flatten

      a.onComplete {
        case Failure(exception) => logger.info(s"Data store failed $exception")
        case Success(value) => logger.info(s"Data stored successfully $value")
      }
  }
}
