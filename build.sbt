name := "security"

version := "1.0"


scalaVersion := "2.12.4"

val akkaVersion = "2.6.9"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-http" % "10.2.0",
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.lightbend.akka" %% "akka-stream-alpakka-slick" % "2.0.2",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0",
  "org.json4s" %% "json4s-native" % "3.6.8",
  "mysql" % "mysql-connector-java" % "8.0.21",
  "com.pauldijou" %% "jwt-json4s-native" % "4.2.0"
)