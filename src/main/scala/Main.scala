import actors.PollActor
import akka.actor.{ActorSystem, Props}
import api.Docs.swaggerUIServerEndpoints
import api.{ApiServer, EndpointsLogic}
import config.{DatabaseConfig, MigrationConfig}
import daos.DataDao
import processing.SensorDataProcessor

import java.time.OffsetDateTime
import java.util.concurrent.{Executors, TimeUnit}
import java.util.logging.Logger
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.Duration

object Main extends App with MigrationConfig with DatabaseConfig {
  implicit val as: ActorSystem = ActorSystem()
  implicit val ec: ExecutionContext = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(100))

  private val logger = Logger.getLogger("main")

  val serviceStartTime = OffsetDateTime.now()

  //migrate database
  migrate()

  val dataDao = new DataDao()
  val sensorDataProcessor = new SensorDataProcessor(dataDao)
  val apiServerLogic = new EndpointsLogic(dataDao)

  val pollActor = as.actorOf(Props(new PollActor(serviceStartTime, sensorDataProcessor)))

  as.scheduler.scheduleWithFixedDelay(
    initialDelay = Duration(1, TimeUnit.MINUTES),
    delay = Duration(5, TimeUnit.MINUTES),
    receiver = pollActor,
    message = "Poll"
  )

  ApiServer.startServer(apiServerLogic.serverEndpoints ++ swaggerUIServerEndpoints)
  logger.info("Try out the API by opening the Swagger UI: http://localhost:8080/docs")

}
