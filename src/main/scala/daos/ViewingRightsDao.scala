package daos

import daos.PostgresDriver.api._
import domain.{Account, ViewingRight}

import scala.concurrent.ExecutionContext

final class ViewingRightsDao(implicit ec: ExecutionContext) {
  val dao: PostgresDriver.api.TableQuery[ViewingRightsSchema] = TableQuery[ViewingRightsSchema]

  def insertIfNotExist(viewingRight: ViewingRight): DBIO[Int] =
    for {
      exists <- dao
        .filter(_.accountId === viewingRight.accountId)
        .filter(_.deviceId === viewingRight.deviceId)
        .exists
        .result
      result <- if (!exists) dao += viewingRight
      else DBIO.successful(0)
    } yield result

  def get(deviceId: String, accountId: Int): DBIO[Seq[ViewingRight]] =
    dao.filter(_.deviceId === deviceId).filter(_.accountId === accountId).result

  def get(deviceId: String): DBIO[Seq[ViewingRight]] =
    dao.filter(_.deviceId === deviceId).result
}

final class ViewingRightsSchema(tag: Tag) extends Table[ViewingRight](tag, "viewing_rights") {

  def deviceId = column[String]("device_id")

  def accountId = column[Int]("account_id")

  override def * : slick.lifted.ProvenShape[ViewingRight] =
    (deviceId, accountId).<>(
      ViewingRight.tupled,
      ViewingRight.unapply,
    )
}
