import actors.PollActorHub
import akka.actor.{ActorRef, ActorSystem, Props}
import api.Docs.uiServerEndpoints
import api.{ApiServer, EndpointsLogic}
import config.{Config, DatabaseConfig, MigrationConfig}
import daos.{AccountDao, DataDao, DeviceDao, LocationDao, ViewingRightsDao}
import processing.SensorDataProcessor

import java.time.OffsetDateTime
import java.util.concurrent.Executors
import java.util.logging.Logger
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext}

object Main extends App with MigrationConfig with DatabaseConfig with Config {
  implicit val as: ActorSystem = ActorSystem()
  implicit val ec: ExecutionContext = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(100))

  private val logger = Logger.getLogger("main")

  val serviceStartTime = OffsetDateTime.now()

  logger.info("Migrating the database")
  migrate()

  //daos
  val dataDao = new DataDao()
  val accountDao = new AccountDao()
  val viewingRightsDao = new ViewingRightsDao()
  val locationDao = new LocationDao()
  val deviceDao = new DeviceDao(viewingRightsDao, accountDao)
  val sensorDataProcessor = new SensorDataProcessor(dataDao, locationDao)

  val pollActorHub: ActorRef = as.actorOf(Props(new PollActorHub(serviceStartTime, sensorDataProcessor, deviceDao)))

  val apiServerLogic = new EndpointsLogic(dataDao, accountDao, deviceDao, viewingRightsDao, locationDao, pollActorHub)

  val allDevicesFuture = deviceDao.getAll
  allDevicesFuture.collect(
    _.map { device =>
      pollActorHub ! device
    }
  )

  logger.info("Starting api server")
  ApiServer.startServer(apiServerLogic.serverEndpoints ++ uiServerEndpoints, interface)
  logger.info("Try out the API by opening the Swagger UI: http://localhost:8080/docs")

  Await.result(as.whenTerminated, 100.days)
}
