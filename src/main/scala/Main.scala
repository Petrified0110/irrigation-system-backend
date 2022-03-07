import actors.PollActor
import akka.actor.{ActorSystem, Props}
import config.{DatabaseConfig, MigrationConfig}
import daos.{DataDao, DbOperations}
import processing.SensorDataProcessor

import java.time.OffsetDateTime
import java.util.concurrent.{Executors, TimeUnit}
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.Duration

object Main extends App with MigrationConfig with DatabaseConfig {
  implicit val as: ActorSystem = ActorSystem()
  implicit val ec: ExecutionContext = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(100))

  val serviceStartTime = OffsetDateTime.now()

  //migrate database
  migrate()

  val dataDao = new DataDao()
  val sensorDataProcessor = new SensorDataProcessor(dataDao)

  val pollActor = as.actorOf(Props(new PollActor(serviceStartTime, sensorDataProcessor)))

  as.scheduler.scheduleWithFixedDelay(
    initialDelay = Duration(1, TimeUnit.MINUTES),
    delay = Duration(5, TimeUnit.MINUTES),
    receiver = pollActor,
    message = "Poll"
  )

}
