package daos

import slick.dbio.DBIO

import scala.concurrent.{ExecutionContext, Future}

trait DbOperations {

  implicit class DBIOOptionOps[A](dbio: DBIO[Option[A]]) {

    def flattenOption(orElseFail: Throwable)(implicit ec: ExecutionContext): DBIO[A] =
      dbio.flatMap {
        case Some(value) => DBIO.successful(value)
        case None => DBIO.failed(orElseFail)
      }
  }

  def transact[A](dbio: => DBIO[A])(implicit db: PostgresDriver.backend.DatabaseDef): Future[A] =
    db.run(dbio)
}