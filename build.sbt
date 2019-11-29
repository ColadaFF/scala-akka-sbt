name := "scala-akka-sbt"

version := "0.1"

scalaVersion := "2.13.1"

val akkaVersion = "2.6.0"
val akkaHttpVersion = "10.1.10"

// https://mvnrepository.com/artifact/com.typesafe.akka/akka-actor
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion, // json
  "ch.qos.logback" % "logback-classic" % "1.2.3"
)

