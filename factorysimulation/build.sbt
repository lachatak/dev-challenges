name := "factorysimulation"

organization := "org.kaloz"

version := "1.0.0"

scalaVersion := "2.11.4"

scalacOptions ++= Seq("-unchecked", "-deprecation", "-encoding", "utf8")

javacOptions ++= Seq("-Xlint:deprecation", "-encoding", "utf8")

mainClass in assembly := Some("org.kaloz.factorysimulation.ActorFlowGraphFactoryMain")

assemblyJarName in assembly := "factorysimulation.jar"

libraryDependencies ++= Seq(
  "org.scalaz" %% "scalaz-core" % "7.1.1",
  "org.apache.commons" % "commons-lang3" % "3.0",
  "com.typesafe.akka" %% "akka-actor" % "2.3.11",
  "com.typesafe.akka" %% "akka-stream-experimental" % "1.0-RC4",
  "com.typesafe.akka" %% "akka-http-core-experimental" % "1.0-RC4",
  "com.typesafe.akka" %% "akka-http-experimental" % "1.0-RC4",
  "org.scalaj" %% "scalaj-http" % "1.1.4",
  "com.typesafe.play" %% "play-json" % "2.3.9",
  "com.netflix.hystrix" % "hystrix-core" % "1.4.12"
)

// Test Dependencies
libraryDependencies ++= Seq(
  "org.specs2" %% "specs2-core" % "3.4" % "test",
  "org.specs2" %% "specs2-mock" % "3.4" % "test",
  "org.specs2" %% "specs2-junit" % "3.4" % "test",
  "com.typesafe.akka" %% "akka-testkit" % "2.3.11" % "test"
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
