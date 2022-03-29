package processing

import akka.stream.Materializer
import daos.{DataDao, DbOperations, PostgresDriver}
import domain.{SensorData, SeqCloudSensorData}
import io.circe.generic.auto._
import io.circe.parser.decode

import java.time.{Instant, ZoneOffset}
import java.util.logging.Logger
import scala.concurrent.ExecutionContext

class SensorDataProcessor(dataDao: DataDao)(implicit db: PostgresDriver.backend.DatabaseDef) extends DbOperations {
  private val logger = Logger.getLogger("sensor-data-processor")

  def processAndStore(data: String)(implicit m: Materializer, ec: ExecutionContext) = {
    val decodedData = decode[SeqCloudSensorData](data)

    decodedData match {
      case Right(multipleSensorData) =>
        logger.info(s"Decoded data: $decodedData")

        val data = for {
          sensorData <- multipleSensorData.items
        } yield
          SensorData(
            dataType = sensorData.message.appId,
            data = sensorData.message.data,
            time = Instant.ofEpochMilli(sensorData.message.time).atOffset(ZoneOffset.UTC),
            deviceId = sensorData.deviceId,
            tenantId = sensorData.tenantId
          )

        logger.info(s"Storing data in the db")
        transact(dataDao.getTableRowsCount).collect { numberOfRows =>
          transact(dataDao.insertMany(data, numberOfRows))
        }.flatten

      case Left(_) => sys.error("something wrong")
    }

  }
}
