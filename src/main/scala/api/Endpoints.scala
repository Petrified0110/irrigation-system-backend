package api

import domain.SensorData
import io.circe.generic.auto._
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._

trait Endpoints {
  type AuthToken = String

  private val baseEndpoint = endpoint.errorOut(stringBody).in("v1")

  val getData: PublicEndpoint[(String, Option[String], Option[String]), String, Seq[SensorData], Any] = baseEndpoint.get
    .in("data" / query[String]("sensorType") / query[Option[String]]("from") / query[Option[String]]("to"))
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
