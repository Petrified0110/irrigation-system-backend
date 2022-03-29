val AkkaVersion = "2.6.18"
val AkkaHttpVersion = "10.2.9"
val enumeratumV = "1.6.0"

enablePlugins(
  FlywayPlugin,
  JavaAppPackaging,
  DockerPlugin
)

scalaVersion := "2.13.4"
name := "licenta-project"
version := "1.3"

libraryDependencies ++= Seq(
  "org.postgresql"              % "postgresql"              % "42.3.3",
  "com.typesafe.slick"          %% "slick"                  % "3.3.3",
  "com.typesafe.slick"          %% "slick-hikaricp"         % "3.3.3",
  "org.slf4j"                   % "slf4j-nop"               % "1.7.36",
  "org.flywaydb"                % "flyway-core"             % "8.5.2",
  "com.typesafe.akka"           %% "akka-actor"             % AkkaVersion,
  "com.typesafe.akka"           %% "akka-stream"            % AkkaVersion,
  "com.typesafe.akka"           %% "akka-http"              % AkkaHttpVersion,
  "com.typesafe.akka"           %% "akka-http-spray-json"   % AkkaHttpVersion,
  "io.circe"                    %% "circe-generic"          % "0.14.1",
  "io.circe"                    %% "circe-parser"           % "0.14.1",
  "com.github.tminglei"         %% "slick-pg"               % "0.20.2",
  "com.softwaremill.sttp.tapir" %% "tapir-json-circe"       % "0.20.1",
  "com.softwaremill.sttp.tapir" %% "tapir-akka-http-server" % "0.20.1",
  "com.softwaremill.sttp.tapir" %% "tapir-redoc-bundle"     % "0.20.1",
  "ch.megard"                   %% "akka-http-cors"         % "1.1.3",
  "com.github.t3hnar"           %% "scala-bcrypt"           % "4.3.0",
  "com.github.jwt-scala"        %% "jwt-core"               % "9.0.5",
  "net.liftweb"                 %% "lift-json"              % "3.5.0",
  "org.typelevel"               %% "cats-effect"            % "3.3.8"
)

Compile / mainClass := Some("Main")

maintainer := "Petra Horvath"
