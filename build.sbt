name := "graph-transformer"
version := "0.1.0"
scalaVersion := "3.4.2"

val catsVersion  = "2.12.0"
val zioVersion   = "2.1.6"
val circeVersion = "0.14.9"
val tapirVersion = "1.11.0"
val scalatestVersion = "3.2.9"

lazy val graphTransformer = (project in file("."))
  .settings(
    semanticdbEnabled := true,
    semanticdbVersion := scalafixSemanticdb.revision,
    scalacOptions ++= Seq(
      "-Wunused:imports",
      "-deprecation",
      "-Werror",
    ),
    libraryDependencies ++= Seq(
      // cats
      "org.typelevel" %% "cats-core" % catsVersion,
      // zio
      "dev.zio" %% "zio" % zioVersion,
      // circe
      "io.circe" %% "circe-core" % circeVersion,
      // tapir
      "com.softwaremill.sttp.tapir" %% "tapir-core"             % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-vertx-server-zio" % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-json-circe"       % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-files"            % tapirVersion,
      // test
      "org.scalatest" %% "scalatest" % "3.2.19" % Test,
    )
  )
