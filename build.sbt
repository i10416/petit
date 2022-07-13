import sbt._
import sbtcrossproject.CrossPlugin.autoImport.crossProject

val scala212 = "2.12.16"
val scala213 = "2.13.8"
val scala3 = "3.1.2"
inThisBuild(
  Seq(
    versionScheme := Some("early-semver"),
    organization := "dev.i10416",
    licenses := List(
      "Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0")
    ),
    homepage := Some(url("https://github.com/i10416/petit")),
    developers := List(
      Developer(
        "i10416",
        "i10416",
        "ito.yo16uh90616+petit@gmail.com",
        url("https://github.com/i10416")
      )
    ),
    scalacOptions ++= Seq("-feature", "-deprecation"),
    sonatypeCredentialHost := "s01.oss.sonatype.org",
    sonatypeRepository := "https://s01.oss.sonatype.org/service/local"
  )
)

lazy val lib = crossProject(JSPlatform, JVMPlatform)
  .in(file("petit"))
  .settings(
    name := "petit",
    scalaVersion := scala3,
    scalacOptions ++= {
      if (scalaVersion.value.startsWith("2.12")) Seq("-language:higherKinds")
      else Nil
    },
    crossScalaVersions := Seq(scala212, scala213, scala3),
    libraryDependencies ++= Dependencies.deps ++ Seq(
      "org.typelevel" %%% "cats-effect" % "3.3.14"
    )
  )
  .jsSettings(
    publish / skip := true,
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "2.2.0",
      "co.fs2" %%% "fs2-core" % "3.2.7"
    )
  )
