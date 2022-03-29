package domain

case class Account(
  firstName: Option[String],
  lastName: Option[String],
  email: String,
  password: String
)

case class Credentials(
  email: String,
  password: String
)

case class Auth(
  token: String
)

case class GivenToken(content: String)

case class DecodedToken(
  email: String,
  firstName: Option[String],
  lastName: Option[String],
)
