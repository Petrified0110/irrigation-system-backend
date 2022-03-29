package processing

import akka.actor.ActorSystem
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.Materializer
import api.ApiRoutes
import domain.NrfDevice

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}
import io.circe.generic.auto._
import io.circe.parser.decode

object NrfDeviceProcessor {

  def checkIfDeviceValid(deviceId: String, nrfToken: String, tenantId: UUID)(
    implicit m: Materializer,
    ec: ExecutionContext,
    as: ActorSystem
  ): Future[Either[String, NrfDevice]] = {
    val response = ApiRoutes.getNrfDevice(deviceId, nrfToken)

    response
      .flatMap(Unmarshal(_).to[String])
      .collect { stringData =>
        decode[NrfDevice](stringData) match {
          case Right(nrfDevice) =>
            if (nrfDevice.tenantId == tenantId)
              Right(nrfDevice)
            else
              Left("The provided tenant id doesn't correspond to the device")
          case Left(_) => Left("A device with the provided credentials doesn't exist")

        }
      }
  }

}
