package processing

import akka.stream.Materializer
import daos.{DataDao, DbOperations, PostgresDriver}
import domain.{SensorData, SeqCloudSensorData}
import io.circe.generic.auto._
import io.circe.parser.decode

import java.time.{Instant, ZoneOffset}
import scala.concurrent.ExecutionContext

class SensorDataProcessor(dataDao: DataDao)(implicit db: PostgresDriver.backend.DatabaseDef) extends DbOperations {

  def processAndStore(data: String)(implicit m: Materializer, ec: ExecutionContext) = {
    val decodedData = decode[SeqCloudSensorData](data)

    decodedData match {
      case Right(multipleSensorData) =>
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

        transact(dataDao.insertMany(data))

      case Left(_) => sys.error("something wrong")
    }

  }
}
