package domain

import java.time.OffsetDateTime
import java.util.UUID

case class Device(
  deviceId: String,
  tenantId: UUID,
  nrfToken: String,
  owner: Int,
  createdTime: OffsetDateTime,
  lastPoll: Option[OffsetDateTime],
  deviceName: Option[String]
)
