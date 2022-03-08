val AkkaVersion = "2.6.16"
val AkkaHttpVersion = "10.2.9"
val enumeratumV = "1.6.0"

enablePlugins(
  JavaServerAppPackaging
)

scalaVersion := "2.13.4"
name := "licenta-project"
version := "1.0"

libraryDependencies ++= Seq(
  "org.postgresql"              % "postgresql"               % "42.3.3",
  "com.typesafe.slick"          %% "slick"                   % "3.3.3",
  "com.typesafe.slick"          %% "slick-hikaricp"          % "3.3.3",
  "org.slf4j"                   % "slf4j-nop"                % "1.7.36",
  "org.flywaydb"                % "flyway-core"              % "8.5.1",
  "com.typesafe.akka"           %% "akka-actor"              % AkkaVersion,
  "com.typesafe.akka"           %% "akka-stream"             % AkkaVersion,
  "com.typesafe.akka"           %% "akka-http"               % AkkaHttpVersion,
  "com.typesafe.akka"           %% "akka-http-spray-json"    % AkkaHttpVersion,
  "io.circe"                    %% "circe-generic"           % "0.14.1",
  "io.circe"                    %% "circe-parser"            % "0.14.1",
  "com.github.tminglei"         %% "slick-pg"                % "0.20.2",
  "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % "0.20.1",
  "com.softwaremill.sttp.tapir" %% "tapir-json-circe"        % "0.20.1",
  "com.softwaremill.sttp.tapir" %% "tapir-akka-http-server"  % "0.20.1"
)

enablePlugins(FlywayPlugin)
enablePlugins(OpenapiCodegenPlugin)

openapiSwaggerFile := baseDirectory.value / "swagger.yaml"

Compile / mainClass := Some("Main")

maintainer := "Petra Horvath"
