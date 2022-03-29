package api
import actors.PollActorHub
import akka.actor.{ActorRef, ActorSystem}
import akka.stream.Materializer
import cats.data.EitherT
import cats.implicits._
import daos._
import domain._
import processing.NrfDeviceProcessor.checkIfDeviceValid
import security.{Encryption, JWT}
import sttp.model.StatusCode
import sttp.tapir.server.ServerEndpoint

import java.time.OffsetDateTime
import java.util.logging.Logger
import scala.concurrent.{ExecutionContext, Future}

class EndpointsLogic(
  dataDao: DataDao,
  accountDao: AccountDao,
  deviceDao: DeviceDao,
  viewingRightsDao: ViewingRightsDao,
  pollActorHub: ActorRef
)(
  implicit m: Materializer,
  ec: ExecutionContext,
  as: ActorSystem,
  db: PostgresDriver.backend.DatabaseDef
) extends Endpoints
    with DbOperations {

  private val logger = Logger.getLogger("endpoints")

  def serverEndpoints: List[ServerEndpoint[Any, Future]] = {

    def postAccount(
      account: Account
    ): Future[Either[String, StatusCode]] = {
      val encryptedPasswordAccount =
        Account(account.firstName, account.lastName, account.email, Encryption.create(account.password))

      val result = transact(accountDao.insertIfNotExists(encryptedPasswordAccount))

      result.collect {
        case 0 => Left(s"An account with the email ${account.email} already exists")
        case _ => Right(StatusCode.Accepted)
      }
    }

    def getAllAccounts(
      token: String
    ): Future[Either[String, Seq[Account]]] = {
      (for {
        decodedToken <- EitherT(Future.successful(JWT.decodeToken(token)))
        accountIdAndAccount <- EitherT(getAccount(decodedToken.email))

        result: EitherT[Future, String, Seq[Account]] = EitherT(
          transact(accountDao.get())
            .collect(Right(_))
            .recover(error => Left(error.toString))
        )

        resultWithoutEither <- result

      } yield resultWithoutEither).value
    }

    def login(
      credentials: Credentials
    ): Future[Either[String, String]] = {

      getAccount(credentials.email).map { accountEither =>
        for {
          account <- accountEither
          result <- if (Encryption.validate(credentials.password, account._2.password)) {
            Right(JWT.generateToken(DecodedToken(account._2.email, account._2.firstName, account._2.lastName)))
          } else {
            Left("Incorrect password")
          }
        } yield result
      }
    }

    def getDataLogic(
      deviceId: String,
      dataType: Option[String],
      from: Option[String],
      to: Option[String] = Some(OffsetDateTime.now().toString),
      token: String
    ): Future[Either[String, Seq[SensorData]]] = {

      val fromOffsetDateTime = from match {
        case Some(date) => OffsetDateTime.parse(date)
        case None => OffsetDateTime.now().minusDays(1)
      }
      val toOffsetDateTime = to match {
        case Some(date) => OffsetDateTime.parse(date)
        case None => OffsetDateTime.now()
      }

      (for {
        decodedToken <- EitherT(Future.successful(JWT.decodeToken(token)))
        accountIdAndAccount <- EitherT(getAccount(decodedToken.email))
        (accountId, _) = accountIdAndAccount

        device <- EitherT(getDevice(deviceId))

        _ <- EitherT(getViewingRights(deviceId, accountId, device.owner))

        result: EitherT[Future, String, Seq[SensorData]] = EitherT(
          transact(dataDao.getForInterval(deviceId, dataType, fromOffsetDateTime, toOffsetDateTime))
            .collect(Right(_))
            .recover(error => Left(error.toString))
        )

        resultWithoutEither <- result
      } yield resultWithoutEither).value
    }

    def postDevice(token: String, device: DeviceFromFrontend): Future[Either[String, StatusCode]] = {
      val result = (for {
        //check if request is authenticated
        decodedToken <- EitherT(Future.successful(JWT.decodeToken(token)))

        //check if device is a valid nRF cloud device
        _ <- EitherT(checkIfDeviceValid(device.deviceId, device.nrfToken, device.tenantId))

        //check if account exists and retrieve it
        accountIdAndAccount <- EitherT(getAccount(decodedToken.email))
        (accountId, _) = accountIdAndAccount

        deviceToStore = Device(
          deviceId = device.deviceId,
          tenantId = device.tenantId,
          nrfToken = device.nrfToken,
          owner = accountId,
          createdTime = OffsetDateTime.now,
          lastPoll = None,
          deviceName = device.deviceName
        )

        result = transact(deviceDao.insertIfNotExists(deviceToStore)).map {
          case 0 => Left(s"A device with the provided credentials already exists")
          case _ =>
            pollActorHub ! deviceToStore
            Right(StatusCode.Accepted)
        }
      } yield result).value.flatMap {
        case Right(value) => value
        case Left(value) => Future.successful(Left(value))
      }

      result
    }

    def shareDevice(
      email: String,
      deviceId: String,
      token: String
    ): Future[Either[String, StatusCode]] = {
      (for {
        //authenticate
        decodedToken <- EitherT(Future.successful(JWT.decodeToken(token)))
        accountIdAndAccountCurrent <- EitherT(getAccount(decodedToken.email))
        (accountIdCurrent, _) = accountIdAndAccountCurrent

        //check if account to share with exists
        accountIdAndAccountToShareWith <- EitherT(getAccount(email))
        (accountIdToShareWith, _) = accountIdAndAccountToShareWith

        //check if device exists and its owner is the current user
        device <- EitherT(getDevice(deviceId))
        _ <- EitherT(
          if (device.owner != accountIdCurrent)
            Future.successful(Left("User can't share this device since it's not theirs"))
          else Future.successful(Right()))

        viewingRights = ViewingRight(deviceId = deviceId, accountId = accountIdToShareWith)

        result = transact(viewingRightsDao.insert(viewingRights)).map {
          case 0 => Left("Viewing rights already exist")
          case _ => Right(StatusCode.Accepted)
        }
      } yield result).value.flatMap {
        case Right(value) => value
        case Left(value) => Future.successful(Left(value))
      }
    }

    def getDevices(token: String): Future[Either[String, Seq[DeviceFromFrontend]]] =
      (for {
        //authenticate
        decodedToken <- EitherT(Future.successful(JWT.decodeToken(token)))
        accountIdAndAccount <- EitherT(getAccount(decodedToken.email))
        (accountId, _) = accountIdAndAccount

        result: EitherT[Future, String, Seq[(Device, Account)]] = EitherT(
          transact(deviceDao.get(accountId))
            .collect(Right(_))
            .recover(error => Left(error.toString)))

        resultWithoutEither <- result
      } yield
        resultWithoutEither.map { deviceAndAccount =>
          val (device, account) = deviceAndAccount

          DeviceFromFrontend(
            deviceId = device.deviceId,
            tenantId = device.tenantId,
            nrfToken = device.nrfToken,
            deviceName = device.deviceName,
            owner = Some(account.email)
          )
        }).value

    List(
      createDeviceEndpoint.serverLogic((postDevice _).tupled),
      shareDeviceEndpoints.serverLogic((shareDevice _).tupled),
      getDevicesEndpoint.serverLogic(getDevices),
      getDataEndpoint.serverLogic((getDataLogic _).tupled),
      createAccountEndpoint.serverLogic(postAccount),
      loginEndpoint.serverLogic(login),
      getAllAccountsEndpoint.serverLogic(getAllAccounts),
      healthEndpoint.serverLogic { _ =>
        Future.successful[Either[StatusCode, Unit]](Right("pong"))
      }
    )
  }

  private def getAccount(email: String): Future[Either[String, (Int, Account)]] = {
    val accountSeqTry = transact(accountDao.get(email))

    accountSeqTry.collect { accounts =>
      accounts.size match {
        case 0 => Left("No account exists with this email")
        case 1 => Right(accounts.head)
        case _ => Left("Something is wrong, more accounts with the same email")
      }
    }
  }

  private def getDevice(deviceId: String): Future[Either[String, Device]] = {
    val devicesSeqTry = transact(deviceDao.get(deviceId))

    devicesSeqTry.collect { devices =>
      devices.size match {
        case 0 => Left("No account exists with this email")
        case 1 => Right(devices.head)
        case _ => Left("Something is wrong, more accounts with the same email")
      }
    }
  }

  private def getViewingRights(
    deviceId: String,
    accountId: Int,
    deviceOwner: Int): Future[Either[String, ViewingRight]] = {
    if (deviceOwner == accountId)
      return Future.successful(Right(ViewingRight("", 0)))

    val viewingRightsSeqTry = transact(viewingRightsDao.get(deviceId, accountId))

    viewingRightsSeqTry.collect { viewingRights =>
      viewingRights.size match {
        case 0 => Left("The account doesn't have access to the device")
        case _ => Right(viewingRights.head)
      }
    }
  }
}
