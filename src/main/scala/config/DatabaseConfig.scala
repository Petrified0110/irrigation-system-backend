package config

import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import daos.PostgresDriver
import slick.util.AsyncExecutor

trait DatabaseConfig extends Config {

  val executor = AsyncExecutor(
    name = "hikari-async-executor",
    minThreads = 5,
    maxThreads = 5,
    queueSize = 500,
    maxConnections = 5,
  )
  val hikariConfig = new HikariConfig()
  hikariConfig.setJdbcUrl(databaseUrl)
  hikariConfig.setUsername(databaseUser)
  hikariConfig.setPassword(databasePassword)

  val hikari = new HikariDataSource(hikariConfig)

  implicit val databaseRef: PostgresDriver.backend.DatabaseDef =
    PostgresDriver.api.Database.forDataSource(
      ds = hikari,
      maxConnections = Option(5),
      executor = executor,
    )

//  def db = Database.forConfig("database")
}
