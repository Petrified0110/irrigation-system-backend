package config

import org.flywaydb.core.Flyway

trait MigrationConfig extends Config {

  val connectionString = databaseUrl

  private val flyway = Flyway
    .configure()
    .locations("db/migrations")
    .dataSource(databaseUrl, databaseUser, databasePassword)
    .load()

  def migrate() = flyway.migrate()

  def reloadSchema() = {
    flyway.clean()
    flyway.migrate()
  }
}
