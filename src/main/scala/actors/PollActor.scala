package actors

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.http.scaladsl.unmarshalling.Unmarshal
import api.ApiRoutes
import daos.PostgresDriver
import processing.SensorDataProcessor

import java.time.OffsetDateTime
import scala.util.{Failure, Success}

object PollActor {
  def props: Props = Props[PollActor]
}

class PollActor(serviceStartTime: OffsetDateTime, sensorDataProcessor: SensorDataProcessor)(
  implicit as: ActorSystem,
  db: PostgresDriver.backend.DatabaseDef
)
    extends Actor
    with ActorLogging {

  import context.dispatcher

  var from: OffsetDateTime = serviceStartTime

  def receive = {
    case "Poll" =>
      val to = OffsetDateTime.now()

      val response = ApiRoutes.pollMessages("nrf-352656100442659", Some(from), Some(to))
      from = to

      val a = response
        .flatMap(Unmarshal(_).to[String])
        .collect{ stringData =>
          sensorDataProcessor.processAndStore(stringData)
        }.flatten

      a.onComplete {
        case Failure(exception) => println(exception)
        case Success(value) => println(value)
      }
  }
}
