package config

//Load Configuration files

import com.typesafe.config.ConfigFactory

trait Config {

  //set's up ConfigFactory to read from application.conf
  private val config = ConfigFactory.load()

  //Get configurations key vales for database
  private val databaseConfig = config.getConfig("database")

  val databaseUrl = databaseConfig.getString("url")
  val databaseUser = databaseConfig.getString("user")
  val databasePassword = databaseConfig.getString("password")

  val databaseMaxDataLines = databaseConfig.getInt("maxDataLines")

  private val http = config.getConfig("http")
  val interface = http.getString("interface")
}
