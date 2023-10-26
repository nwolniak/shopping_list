name := """shopping-list"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "3.3.1"

libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick" % "3.5.0-M4",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.5.0-M4",
  "com.typesafe.play" %% "play-slick" % "5.2.0-RC1",
  "com.typesafe.play" %% "play-slick-evolutions" % "5.2.0-RC1",
  "org.postgresql" % "postgresql" % "42.6.0",
  "com.h2database" % "h2" % "2.2.224" % Test,
  "com.typesafe.play" %% "play-guice" % "2.9.0-RC3",
  "org.scalatestplus.play" %% "scalatestplus-play" % "6.0.0-RC2" % Test
)

libraryDependencies ++= Seq(guice, evolutions) // jdbc
