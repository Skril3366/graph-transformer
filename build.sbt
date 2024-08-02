name := "graph-transformer"
version := "0.1.0"
scalaVersion := "3.4.2"

lazy val graphTransformer = (project in file("."))
  .settings(
    libraryDependencies ++= Seq(
      // cats
      "org.typelevel" %% "cats-core" % "2.12.0",
      // zio
      "dev.zio" %% "zio" % "2.1.6",
      // test
      "org.scalatest" %% "scalatest" % "3.2.19" % Test,
    )
  )
