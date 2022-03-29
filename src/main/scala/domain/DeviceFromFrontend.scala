package domain

import java.util.UUID

case class DeviceFromFrontend(
  deviceId: String,
  tenantId: UUID,
  nrfToken: String,
  deviceName: Option[String],
  owner: Option[String]
)
