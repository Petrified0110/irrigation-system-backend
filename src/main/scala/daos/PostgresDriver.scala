package daos

import com.github.tminglei.slickpg._
import slick.basic.Capability

trait PostgresDriver extends ExPostgresProfile with PgDate2Support with PgArraySupport {

  protected trait BaseAPI extends API with DateTimeImplicits with ArrayImplicits

  override protected def computeCapabilities: Set[Capability] =
    super.computeCapabilities + slick.jdbc.JdbcCapabilities.insertOrUpdate

  override val api: BaseAPI = new BaseAPI {}

}

object PostgresDriver extends PostgresDriver

