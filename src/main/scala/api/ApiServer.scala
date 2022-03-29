package api

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods
import ch.megard.akka.http.cors.scaladsl.CorsDirectives.cors
import ch.megard.akka.http.cors.scaladsl.model.HttpHeaderRange
import ch.megard.akka.http.cors.scaladsl.settings.CorsSettings
import sttp.tapir.server.ServerEndpoint

import java.util.logging.Logger
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

object ApiServer {
  private val logger = Logger.getLogger("api-server")

  val allowedMethods = Seq(HttpMethods.GET, HttpMethods.POST, HttpMethods.PUT, HttpMethods.DELETE)
  val allowedHeaders = Seq(HttpHeaderRange.*)

  val corsSettings: CorsSettings = CorsSettings.defaultSettings
    .withAllowedMethods(allowedMethods)
    .withAllowedHeaders(HttpHeaderRange.*)

  def startServer(serverEndpoints: List[ServerEndpoint[Any, Future]], interface: String)(
    implicit as: ActorSystem): Unit = {
    import sttp.tapir.server.akkahttp.AkkaHttpServerInterpreter

    val routes = cors(corsSettings) { AkkaHttpServerInterpreter().toRoute(serverEndpoints) }
    Await.result(Http().newServerAt(interface, 8080).bindFlow(routes), 1.minute)

    logger.info("Server started")
  }
}
