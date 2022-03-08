package api

import sttp.tapir.server.ServerEndpoint
import sttp.tapir.swagger.bundle.SwaggerInterpreter

import scala.concurrent.Future

object Docs extends Endpoints {

  def swaggerUIServerEndpoints: List[ServerEndpoint[Any, Future]] = {
    SwaggerInterpreter().fromEndpoints(List(getData), "The Tapir Library", "1.0")
  }
}
