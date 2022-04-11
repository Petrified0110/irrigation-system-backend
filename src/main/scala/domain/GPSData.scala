package domain

import java.time.OffsetDateTime

case class GPSData(
  deviceId: String,
  time: OffsetDateTime,
  latitude: Double,
  longitude: Double,
)
