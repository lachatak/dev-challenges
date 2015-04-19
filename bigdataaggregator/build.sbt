name := "bigdataaggregator"

organization := "org.kaloz"

version := "1.0.0"

scalaVersion := "2.11.4"

scalacOptions ++= Seq("-unchecked", "-deprecation", "-encoding", "utf8")

javacOptions ++= Seq("-Xlint:deprecation", "-encoding", "utf8", "-XX:MaxPermSize=256M")

mainClass in assembly := Some("org.kaloz.bigdataaggregator.Main")

libraryDependencies ++= Seq(
  "org.scalaz" %% "scalaz-core" % "7.1.1",
  "org.specs2" %% "specs2-core" % "3.4" % "test",
  "org.specs2" %% "specs2-mock" % "3.4",
  "org.specs2" %% "specs2-junit" % "3.4"
)

assemblyExcludedJars in assembly := {
  val cp = (fullClasspath in assembly).value
  cp filter {_.data.getName == "mockito-core-1.9.5.jar"}
}

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
