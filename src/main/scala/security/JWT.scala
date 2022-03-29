package security

import domain.{DecodedToken, GivenToken, SeqCloudSensorData}
import io.circe
import net.liftweb.json.Serialization.write
import net.liftweb.json._
import pdi.jwt.{Jwt, JwtClaim}
import io.circe.generic.auto._
import io.circe.parser.decode

import java.time.Clock
import java.util.logging.Logger
import scala.language.implicitConversions

object JWT {

  private val logger = Logger.getLogger("jwt")

  implicit val formats = DefaultFormats

  def generateToken(token: DecodedToken): String = {
    val startTime = Clock.systemUTC()
    val claim = JwtClaim(write(token)).issuedNow(startTime).expiresIn(24 * 60 * 60)(startTime)
    Jwt.encode(write(claim))
  }

  def decodeToken(token: String): Either[String, DecodedToken] = {
    val pureToken = token.substring("Bearer ".length)

    for {
      token <- eitherWithThrowableToEitherWithString(Jwt.decode(pureToken).toEither)
      contentString <- eitherWithThrowableToEitherWithString(decode[GivenToken](token.content))
      decodedToken <- eitherWithThrowableToEitherWithString(
        decode[DecodedToken](contentString.content.replace("\\\"", "")))
    } yield decodedToken
  }

  private def eitherWithThrowableToEitherWithString[A](either: Either[Throwable, A]): Either[String, A] = {
    either match {
      case Right(value) => Right(value)
      case Left(value) => Left(value.getMessage)
    }
  }

}
