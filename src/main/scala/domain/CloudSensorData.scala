package domain

import java.time.OffsetDateTime
import java.util.UUID


case class Data(
  time: Long,
  data: String,
  messageType: String,
  appId: String
)

case class CloudSensorData(
  topic: String,
  deviceId: String,
  receivedAt: OffsetDateTime,
  message: Data,
  tenantId: UUID
)

case class SeqCloudSensorData(
  items: Seq[CloudSensorData]
)

//object SeqCloudSensorData {
//  implicit val seqCloudSensorDataFormat: Format[SeqCloudSensorData] = Json.format[SeqCloudSensorData]
//}
//
//object CloudSensorData {
//
//  implicit val cloudSensorDataReads: Reads[CloudSensorData] = (
//    (JsPath \ "topic").read[String] and
//      (JsPath \ "deviceId").read[String] and
//      (JsPath \ "receivedAt").read[OffsetDateTime] and
//      (JsPath \ "message").read[Data] and
//      (JsPath \ "tenantId").read[UUID]
//  )(CloudSensorData.apply _)
//
//  implicit val cloudSensorDataWrites: Writes[CloudSensorData] = (
//    (JsPath \ "topic").write[String] and
//      (JsPath \ "deviceId").write[String] and
//      (JsPath \ "receivedAt").write[OffsetDateTime] and
//      (JsPath \ "message").write[Data] and
//      (JsPath \ "tenantId").write[UUID]
//  )(unlift(CloudSensorData.unapply))
//}
//
//object Data {
//
//  implicit val dataReads: Reads[Data] = (
//    (JsPath \ "time").read[Long] and
//      (JsPath \ "data").read[String] and
//      (JsPath \ "messageType").read[String] and
//      (JsPath \ "appId").read[String]
//  )(Data.apply _)
//
//  implicit val dataWrites: Writes[Data] = (
//    (JsPath \ "time").write[Long] and
//      (JsPath \ "data").write[String] and
//      (JsPath \ "messageType").write[String] and
//      (JsPath \ "appId").write[String]
//  )(unlift(Data.unapply))
//}
