package daos

import daos.PostgresDriver.api._
import domain.SensorData

import java.time.OffsetDateTime
import java.util.UUID
import scala.concurrent.ExecutionContext

final class DataDao(implicit ec: ExecutionContext) {
  val dao: PostgresDriver.api.TableQuery[DataSchema] = TableQuery[DataSchema]

  def insert(data: SensorData): DBIO[Int] =
    dao += data

  def insertMany(data: Seq[SensorData]): DBIO[Option[Int]] =
    dao ++= data
}

final class DataSchema(tag: Tag) extends Table[SensorData](tag, "data_table") {

  def id = column[Int]("id", O.PrimaryKey)

  def dataType = column[String]("data_type")

  def data = column[String]("data")

  def time = column[OffsetDateTime]("time")

  def deviceId = column[String]("device_id")

  def tenantId = column[UUID]("tenant_id")

  override def * : slick.lifted.ProvenShape[SensorData] =
    (dataType, data, time, deviceId, tenantId).<>(
      SensorData.tupled,
      SensorData.unapply,
    )
}
