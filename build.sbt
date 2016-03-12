name := """rj_play_twitterstream_sse"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.0-RC1" % Test,
    "org.twitter4j" % "twitter4j-core" % "4.0.2",
  "org.twitter4j" % "twitter4j-async" % "4.0.2",
  "org.twitter4j" % "twitter4j-stream" % "4.0.2",
  "org.twitter4j" % "twitter4j-media-support" % "4.0.2"
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
