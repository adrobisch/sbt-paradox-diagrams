import sbt.url
import sbtrelease.ReleaseStateTransformations._

enablePlugins(SbtPlugin)
addSbtPlugin("com.lightbend.paradox" % "sbt-paradox" % "0.6.5")

name := "sbt-paradox-diagrams"
organization := "com.wanari"
organizationName := "Wanari Ltd."

scalaVersion := "2.12.10"

scalafmtOnCompile := true

sbtPlugin := true

releasePublishArtifactsAction := PgpKeys.publishSigned.value

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  publishArtifacts,
  setNextVersion,
  commitNextVersion,
  releaseStepCommandAndRemaining("sonatypeReleaseAll"),
  pushChanges
)

publishTo := Some(
  if (isSnapshot.value)
    Opts.resolver.sonatypeSnapshots
  else
    Opts.resolver.sonatypeStaging
)

publishMavenStyle := true
licenses := Seq("APL2" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))
homepage := Some(url("https://github.com/TeamWanari/sbt-paradox-diagrams"))
scmInfo := Some(
  ScmInfo(
    url("https://github.com/TeamWanari/sbt-paradox-diagrams"),
    "scm:git@github.com:TeamWanari/sbt-paradox-diagrams.git"
  )
)

publishM2 := {
  publishM2.value

  val d = file(sys.env("HOME")) / s".m2/repository/com/wanari/sbt-paradox-diagrams_${scalaBinaryVersion.value}_${sbtBinaryVersion.value}"
  d.renameTo(file(sys.env("HOME")) / ".m2/repository/com/wanari/sbt-paradox-diagrams")
}

import scala.collection.JavaConverters._
scriptedLaunchOpts += ("-Dproject.version=" + version.value)
scriptedLaunchOpts ++= java.lang.management.ManagementFactory.getRuntimeMXBean.getInputArguments.asScala.filter(
  a => Seq("-Xmx", "-Xms", "-XX", "-Dfile").exists(a.startsWith)
)

lazy val debugScripted = if (sys.env.getOrElse("DEBUG_SCRIPTED", "false").toBoolean) {
  "y"
} else {
  "n"
}

scriptedLaunchOpts += (s"-agentlib:jdwp=transport=dt_socket,server=y,suspend=${debugScripted},address=0")
scriptedBufferLog := false
