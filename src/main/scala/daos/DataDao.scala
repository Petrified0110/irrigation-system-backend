package daos

import config.Config
import daos.PostgresDriver.api._
import domain.SensorData

import java.time.OffsetDateTime
import java.util.UUID
import scala.concurrent.ExecutionContext

final class DataDao(implicit ec: ExecutionContext) extends Config {
  val dao: PostgresDriver.api.TableQuery[DataSchema] = TableQuery[DataSchema]

  def insert(data: SensorData, currentNumberOfRowsInTable: Int): DBIO[Unit] =
    (for {
      _ <- cleanTableBeforeInsert(1, currentNumberOfRowsInTable)
      _ <- dao += data
    } yield ()).transactionally

  def insertMany(data: Seq[SensorData], currentNumberOfRowsInTable: Int): DBIO[Unit] =
    (for {
      _ <- cleanTableBeforeInsert(data.size, currentNumberOfRowsInTable)
      _ <- dao ++= data
    } yield ()).transactionally

  def getForInterval(
    deviceId: String,
    dataType: Option[String],
    from: OffsetDateTime,
    to: OffsetDateTime): DBIO[Seq[SensorData]] = {
    val partialResult = dao.filter(_.deviceId === deviceId).filter(_.time < to).filter(_.time > from)

    if (dataType.isDefined)
      partialResult.filter(_.dataType === dataType).result
    else
      partialResult.result
  }

  def getTableRowsCount: DBIO[Int] =
    dao.size.result

  private def cleanTableBeforeInsert(howMany: Int, currentSize: Int): DBIO[Int] =
    if (currentSize == databaseMaxDataLines)
      dao.filter(_.id in dao.sortBy(_.time).take(howMany).map(_.id)).delete
    else if (currentSize > databaseMaxDataLines)
      dao.filter(_.id in dao.sortBy(_.time).take(currentSize - databaseMaxDataLines - howMany).map(_.id)).delete
    else DBIO.successful(0)

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
