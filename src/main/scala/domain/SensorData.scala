package domain

import java.time.OffsetDateTime
import java.util.UUID

case class SensorData(
  dataType: String,
  data: String,
  time: OffsetDateTime,
  deviceId: String,
  tenantId: UUID
)
