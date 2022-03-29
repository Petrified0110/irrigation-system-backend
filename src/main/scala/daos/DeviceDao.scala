package daos

import daos.PostgresDriver.api._
import domain.{Account, Device}

import java.time.OffsetDateTime
import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

final class DeviceDao(viewingRightsDao: ViewingRightsDao, accountDao: AccountDao)(
  implicit ec: ExecutionContext,
  db: PostgresDriver.backend.DatabaseDef)
    extends DbOperations {
  val dao: PostgresDriver.api.TableQuery[DeviceSchema] = TableQuery[DeviceSchema]

  def insertIfNotExists(device: Device): DBIO[Int] =
    for {
      exists <- dao.filter(_.deviceId === device.deviceId).exists.result
      result <- if (!exists) dao += device
      else DBIO.successful(0)
    } yield result

  def get(deviceId: String): DBIO[Seq[Device]] =
    dao.filter(_.deviceId === deviceId).result

  def get(accountId: Int): DBIO[Seq[(Device, Account)]] = {
    val viewingDevices = for {
      (d, _) <- (dao join viewingRightsDao.dao on (_.deviceId === _.deviceId)).filter(_._2.accountId === accountId)
      a <- accountDao.dao.filter(_.id === d.owner)
    } yield (d, a)

    val ownedDevices = for {
      d <- dao.filter(_.owner === accountId)
      a <- accountDao.dao.filter(_.id === d.owner)
    } yield (d, a)

    (viewingDevices ++ ownedDevices).result
  }

  def getAll: Future[Seq[Device]] =
    transact(dao.result)

  def updateLastPoll(deviceId: String, poll: OffsetDateTime): Future[Int] =
    transact(dao.filter(_.deviceId === deviceId).map(_.lastPoll).update(Some(poll)))

}

final class DeviceSchema(tag: Tag) extends Table[Device](tag, "devices") {
  def deviceId = column[String]("device_id", O.PrimaryKey)

  def tenantId = column[UUID]("tenant_id")

  def nrfToken = column[String]("nrf_token")

  def owner = column[Int]("owner")

  def createdTime = column[OffsetDateTime]("created_time")

  def lastPoll = column[Option[OffsetDateTime]]("last_poll")

  def deviceName = column[Option[String]]("device_name")

  override def * : slick.lifted.ProvenShape[Device] =
    (deviceId, tenantId, nrfToken, owner, createdTime, lastPoll, deviceName).<>(
      Device.tupled,
      Device.unapply,
    )
}
