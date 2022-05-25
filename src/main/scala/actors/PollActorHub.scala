package actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import daos.{DeviceDao, PostgresDriver}
import domain.Device
import processing.SensorDataProcessor

import java.time.OffsetDateTime
import java.util.concurrent.TimeUnit
import java.util.logging.Logger
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.Duration

class PollActorHub(serviceStartTime: OffsetDateTime, sensorDataProcessor: SensorDataProcessor, deviceDao: DeviceDao)(
  implicit as: ActorSystem,
  ec: ExecutionContext,
  db: PostgresDriver.backend.DatabaseDef
) extends Actor {
  private val logger = Logger.getLogger(s"poll-actor-hub")

  private var pollActors = Seq.empty[ActorRef]

  override def receive: Receive = {
    case device: Device =>
      val pollActor =
        as.actorOf(Props(new PollActor(serviceStartTime, sensorDataProcessor, device, deviceDao)))
      logger.info(s"Starting scheduler for device ${device.deviceId}")

      as.scheduler.scheduleWithFixedDelay(
        initialDelay = Duration(0, TimeUnit.MINUTES),
        delay = Duration(5, TimeUnit.MINUTES),
        receiver = pollActor,
        message = "Poll"
      )

      pollActors = pollActors :+ pollActor
      //wait for 30 seconds before starting any other schedulers
      Thread.sleep(30000)
  }
}
