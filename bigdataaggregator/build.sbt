name := "bigdataaggregator"

organization := "org.kaloz"

version := "1.0.0"

scalaVersion := "2.11.4"

scalacOptions ++= Seq("-unchecked", "-deprecation", "-encoding", "utf8")

javacOptions ++= Seq("-Xlint:deprecation", "-encoding", "utf8", "-XX:MaxPermSize=256M")

libraryDependencies ++= Seq(
  "org.scalaz" %% "scalaz-core" % "7.1.1",
  "org.specs2" %% "specs2-core" % "3.4" % "test",
  "org.mockito" % "mockito-core" % "1.10.19" % "test",
  "org.specs2" %% "specs2-mock" % "3.4",
  "org.specs2" %% "specs2-junit" % "3.4"
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
