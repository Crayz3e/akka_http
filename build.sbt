import com.typesafe.sbt.packager.docker.ExecCmd

enablePlugins(JavaAppPackaging, AshScriptPlugin)


dockerBaseImage := "openjdk:8-jre-alpine"
packageName in Docker := "calculator"

name := "akkacalculator"

version := "0.1"

scalaVersion := "2.13.5"

val akkaVersion = "2.6.8"
val akkaHttpVersion = "10.2.4"
val circeVersion = "0.12.3"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test,
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,

  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,
  "de.heikoseeberger" %% "akka-http-circe" % "1.36.0",

  "org.scalatest" %% "scalatest" % "3.2.8" % Test,

  "ch.qos.logback" % "logback-classic" % "1.2.3",
)

dockerCommands := dockerCommands.value.map {
  case ExecCmd("CMD", _ @ _*) =>
    ExecCmd("CMD", "/opt/docker/bin/calculator")
  case other =>
    other
}