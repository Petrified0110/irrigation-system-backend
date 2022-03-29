package daos

import daos.PostgresDriver.api._
import domain.Account

import scala.concurrent.ExecutionContext

final class AccountDao(implicit ec: ExecutionContext) {
  val dao: PostgresDriver.api.TableQuery[AccountSchema] = TableQuery[AccountSchema]

  def insertIfNotExists(account: Account): DBIO[Int] =
    for {
      exists <- dao.filter(_.email === account.email).exists.result
      result <- if (!exists) dao += account
      else DBIO.successful(0)
    } yield result

  def get(email: String): DBIO[Seq[(Int, Account)]] = {
    dao.filter(_.email === email).map(account => account.id -> account).result
  }

  def get(id: Int): DBIO[Seq[Account]] = {
    dao.filter(_.id === id).result
  }

  def get(): DBIO[Seq[Account]] = {
    dao.result
  }
}

final class AccountSchema(tag: Tag) extends Table[Account](tag, "accounts") {

  def id = column[Int]("id", O.PrimaryKey)

  def firstName = column[Option[String]]("first_name")

  def lastName = column[Option[String]]("last_name")

  def email = column[String]("email")

  def password = column[String]("password")

  override def * : slick.lifted.ProvenShape[Account] =
    (firstName, lastName, email, password).<>(
      Account.tupled,
      Account.unapply,
    )
}
