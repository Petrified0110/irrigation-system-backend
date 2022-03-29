package api

import domain.{Account, Credentials, Device, DeviceFromFrontend, SensorData}
import io.circe.generic.auto._
import sttp.model.StatusCode
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._

trait Endpoints {
  type AuthToken = String

  private val baseEndpoint = endpoint.errorOut(stringBody).in("v1")

  lazy val healthEndpoint: PublicEndpoint[Unit, StatusCode, Unit, Any] =
    endpoint.get
      .name("health-endpoint")
      .description("Health Check Endpoint")
      .errorOut(statusCode)

  val createAccountEndpoint: PublicEndpoint[Account, String, StatusCode, Any] =
    baseEndpoint.post
      .in("account")
      .in(jsonBody[Account])
      .out(statusCode)
      .name("create-account")

  val getAllAccountsEndpoint: PublicEndpoint[String, String, Seq[Account], Any] = {
    baseEndpoint.get
      .in("accounts")
      .in(header[String]("Authorization"))
      .out(jsonBody[Seq[Account]])
      .name("get-all-accounts")
  }

  val loginEndpoint: PublicEndpoint[Credentials, String, String, Any] =
    baseEndpoint.post
      .in("token")
      .in(jsonBody[Credentials])
      .out(jsonBody[String])
      .name("generate-session-token")

  val createDeviceEndpoint: PublicEndpoint[(String, DeviceFromFrontend), String, StatusCode, Any] =
    baseEndpoint.post
      .in("device")
      .in(header[String]("Authorization"))
      .in(jsonBody[DeviceFromFrontend])
      .out(statusCode)
      .name("create-device")

  val shareDeviceEndpoints: PublicEndpoint[(String, String, String), String, StatusCode, Any] =
    baseEndpoint.post
      .in("device" / "share" / query[String]("email") / query[String]("deviceId"))
      .in(header[String]("Authorization"))
      .out(statusCode)
      .name("share-device")

  val getDevicesEndpoint: PublicEndpoint[String, String, Seq[DeviceFromFrontend], Any] =
    baseEndpoint.get
      .in("devices")
      .in(header[String]("Authorization"))
      .out(jsonBody[Seq[DeviceFromFrontend]])
      .name("get-devices-user-has-access-to")

  val getDataEndpoint: PublicEndpoint[
    (String, Option[String], Option[String], Option[String], String),
    String,
    Seq[SensorData],
    Any] = baseEndpoint.get
    .in(
      "data" / query[String]("deviceId") / query[Option[String]]("sensorType") / query[Option[String]]("from") / query[
        Option[String]]("to"))
    .in(header[String]("Authorization"))
    .out(jsonBody[Seq[SensorData]])

//  // Re-usable parameter description
//  private val limitParameter = query[Option[Int]]("limit").description("Maximum number of books to retrieve")
//
//  val booksListing: PublicEndpoint[Limit, String, Vector[Book], Any] = baseEndpoint.get
//    .in("list" / "all")
//    .in(limitParameter)
//    .out(jsonBody[Vector[Book]])
//
//  val booksListingByGenre: PublicEndpoint[BooksQuery, String, Vector[Book], Any] = baseEndpoint.get
//    .in(("list" / path[String]("genre").map(Option(_))(_.get)).and(limitParameter).mapTo[BooksQuery])
//    .out(jsonBody[Vector[Book]])
}
