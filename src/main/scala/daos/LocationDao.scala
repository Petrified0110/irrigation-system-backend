package daos

import daos.PostgresDriver.api._
import domain.{Account, GPSData}

import java.time.OffsetDateTime
import scala.concurrent.ExecutionContext

final class LocationDao(implicit ec: ExecutionContext) {
  val dao: PostgresDriver.api.TableQuery[LocationSchema] = TableQuery[LocationSchema]

  def insertOrUpdate(location: GPSData): DBIO[Int] = {
    dao.insertOrUpdate(location)
  }

  def get(deviceId: String): DBIO[Seq[GPSData]] = {
    dao.filter(_.deviceId === deviceId).result
  }
}

final class LocationSchema(tag: Tag) extends Table[GPSData](tag, "latest_locations") {

  def deviceId = column[String]("device_id", O.PrimaryKey)

  def time = column[OffsetDateTime]("time")

  def latitude = column[Double]("latitude")

  def longitude = column[Double]("longitude")

  override def * : slick.lifted.ProvenShape[GPSData] =
    (deviceId, time, latitude, longitude).<>(
      GPSData.tupled,
      GPSData.unapply,
    )
}
