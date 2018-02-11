organization := "charlie-world"

name := "scala-shell-script"

version := "0.1.0"

scalaVersion := "2.12.4"

crossScalaVersions := Seq("2.11.11", "2.12.4")

val catsVersion = "1.0.1"

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.3.1",
  "joda-time" % "joda-time" % "2.9.4",
  "org.typelevel" %% "cats-core" % catsVersion,
  "org.typelevel" %% "cats-laws" % catsVersion,
  "org.scalatest" %% "scalatest" % "3.0.1" % Test
)