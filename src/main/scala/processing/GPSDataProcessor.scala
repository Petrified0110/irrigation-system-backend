package processing

import domain.{GPSData, SensorData}

import java.time.{LocalDate, OffsetTime, ZoneOffset}

object GPSDataProcessor {

  def processGpsData(rawSensorData: Seq[SensorData]): Seq[GPSData] = {
    val dateTime = LocalDate.now()

    for {
      rawData <- rawSensorData
      splitValues = rawData.data.split(',')
      timeValues = splitValues(1)
      offsetTime = OffsetTime.of(
        timeValues.slice(0, 2).toInt,
        timeValues.slice(2, 4).toInt,
        timeValues.slice(4, 6).toInt,
        timeValues.slice(7, 9).toInt,
        ZoneOffset.UTC
      )
      offsetDateTime = dateTime.atTime(offsetTime)
    } yield GPSData(rawData.deviceId, offsetDateTime, splitValues(2).toDouble / 100, splitValues(4).toDouble / 100)
  }

}
