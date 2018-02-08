name := """login-2-gether"""
organization := "co.flexserve"

version := "1.0-SNAPSHOT"

lazy val rootX = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.3"

libraryDependencies += guice
libraryDependencies += evolutions
libraryDependencies += specs2 % Test

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-slick" % "3.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "3.0.0",
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test,
  "com.h2database" % "h2" % "1.4.185",
  "ch.qos.logback" % "logback-classic" % "1.1.2"
)

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "co.flexserve.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "co.flexserve.binders._"
