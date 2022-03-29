package api

import sttp.tapir.server.ServerEndpoint
import sttp.tapir.redoc.bundle.RedocInterpreter

import scala.concurrent.Future

object Docs extends Endpoints {

  def uiServerEndpoints: Seq[ServerEndpoint[Any, Future]] =
    RedocInterpreter().fromEndpoints[Future](List(getDataEndpoint, healthEndpoint, createAccountEndpoint), "My App", "1.0")
}

//import akka.actor.ActorSystem
//import akka.http.scaladsl.Http
//import akka.http.scaladsl.server.Directives._
//import sttp.tapir.docs.openapi._
//import sttp.tapir.openapi.OpenAPI
//import sttp.tapir.openapi.circe.yaml._
//import sttp.tapir.swagger.akkahttp.SwaggerAkka
//import scala.io.StdIn
//object ApartmentsApi extends App {
//  private implicit val actorSystem: ActorSystem = ActorSystem()
//  private val openApiDocs: OpenAPI = List(listApartments)
//    .toOpenAPI("The Apartments API", "1.0.0")
//  private val routes = concat(
//    listApartmentsRoute,
//    new SwaggerAkka(openApiDocs.toYaml).routes
//  )
//  val bindingFuture = Http().newServerAt("localhost", 8090).bind(routes)
//  println("Go to: http://localhost:8090/docs")
//  println("Press any key to exit ...")
//  StdIn.readLine()
//}
