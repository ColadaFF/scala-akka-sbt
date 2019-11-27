name := "scala-akka-sbt"

version := "0.1"

scalaVersion := "2.13.1"

// https://mvnrepository.com/artifact/com.typesafe.akka/akka-actor
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.6.0",
  "ch.qos.logback" % "logback-classic" % "1.2.3"
)

