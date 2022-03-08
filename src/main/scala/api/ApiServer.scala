package api

import sttp.tapir.server.ServerEndpoint

import scala.concurrent.Future
import akka.actor.ActorSystem
import akka.http.scaladsl.Http

import java.util.logging.Logger
import scala.concurrent.Await
import scala.concurrent.duration._

object ApiServer {
  private val logger = Logger.getLogger("api-server")

  def startServer(serverEndpoints: List[ServerEndpoint[Any, Future]])(implicit as: ActorSystem): Unit = {
    import sttp.tapir.server.akkahttp.AkkaHttpServerInterpreter

    val routes = AkkaHttpServerInterpreter().toRoute(serverEndpoints)
    Await.result(Http().newServerAt("localhost", 8080).bindFlow(routes), 1.minute)

    logger.info("Server started")
  }
}
