package api
import daos.{DataDao, DbOperations, PostgresDriver}
import domain.SensorData
import sttp.tapir.server.ServerEndpoint

import java.time.OffsetDateTime
import java.util.logging.Logger
import scala.concurrent.{ExecutionContext, Future}

class EndpointsLogic(dataDao: DataDao)(implicit ec: ExecutionContext, db: PostgresDriver.backend.DatabaseDef)
    extends Endpoints
    with DbOperations {

  private val logger = Logger.getLogger("endpoints")

  def serverEndpoints: List[ServerEndpoint[Any, Future]] = {

    def getDataLogic(
      dataType: String,
      from: Option[String],
      to: Option[String] = Some(OffsetDateTime.now().toString)
    ): Future[Either[String, Seq[SensorData]]] = {
      val fromOffsetDateTime = from match {
        case Some(date) => OffsetDateTime.parse(date)
        case None => OffsetDateTime.now().minusDays(1)
      }
      val toOffsetDateTime = to match {
        case Some(date) => OffsetDateTime.parse(date)
        case None => OffsetDateTime.now()
      }

      transact(dataDao.getByTypeAndDate(dataType, fromOffsetDateTime, toOffsetDateTime)).map(Right(_))
    }

//    def bookAddLogic(book: Book, token: AuthToken): Future[Either[String, Unit]] =
//      Future {
//        if (token != "secret") {
//          logger.warning(s"Tried to access with token: $token")
//          Left("Unauthorized access!!!11")
//        } else {
//          logger.info(s"Adding book $book")
//          Library.Books.getAndUpdate(books => books :+ book)
//          Right(())
//        }

//    def bookListingLogic(limit: Limit): Future[Either[String, Vector[Book]]] =
//      Future {
//        Right[String, Vector[Book]](Library.getBooks(BooksQuery(None, limit)))
//      }
//
//    def bookListingByGenreLogic(query: BooksQuery): Future[Either[String, Vector[Book]]] =
//      Future {
//        Right[String, Vector[Book]](Library.getBooks(query))
//      }

    // interpreting the endpoint description and converting it to an akka-http route, providing the logic which
    // should be run when the endpoint is invoked.
    List(
      getData.serverLogic((getDataLogic _).tupled)
    )
  }
}
