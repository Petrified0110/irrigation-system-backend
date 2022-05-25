package api

import sttp.tapir.server.ServerEndpoint
import sttp.tapir.redoc.bundle.RedocInterpreter

import scala.concurrent.Future

object Docs extends Endpoints {

  def uiServerEndpoints: Seq[ServerEndpoint[Any, Future]] =
    RedocInterpreter().fromEndpoints[Future](
      List(
        getDataEndpoint,
//        healthEndpoint,
        createAccountEndpoint,
        getAllAccountsEndpoint,
        loginEndpoint,
        createDeviceEndpoint,
        shareDeviceEndpoints,
        getDevicesEndpoint,
        getAccountsDeviceCanBeSharedWithEndpoint,
//        getForecastForDeviceEndpoint
      ),
      "My App",
      "1.0"
    )
}
