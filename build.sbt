ThisBuild / scalaVersion := "2.13.14"
ThisBuild / organization := "com.example"

lazy val root = (project in file("."))
  .settings(
    name := "chisel_demo",
    libraryDependencies ++= Seq(
      "org.chipsalliance" %% "chisel" % "6.5.0"
    ),
    scalacOptions ++= Seq(
      "-language:reflectiveCalls",
      "-deprecation",
      "-feature",
      "-unchecked"
    ),
    addCompilerPlugin(
      "org.chipsalliance" % "chisel-plugin" % "6.5.0" cross CrossVersion.full
    )
  )
