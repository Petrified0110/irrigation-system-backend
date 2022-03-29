package domain

import java.util.UUID

case class NrfDevice(
  id: String,
  name: String,
  tenantId: UUID,
)
