package security

import com.github.t3hnar.bcrypt._
import java.util.logging.Logger
import scala.util.Success
import scala.util.Failure

object Encryption {

  private val logger = Logger.getLogger("encryption")

  def create(value: String): String = {
    val salt = generateSalt
    value.bcrypt(salt)
  }

  def validate(value: String, hash: String): Boolean = {

    value.isBcryptedSafeBounded(hash) match {
      case Success(result) =>
        result
      case Failure(_) => {
        logger.info("Hash is not safe")
        false
      }
    }

  }

}
