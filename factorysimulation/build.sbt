name := "factorysimulation"

organization := "org.kaloz"

version := "1.0.0"

scalaVersion := "2.11.4"

scalacOptions ++= Seq("-unchecked", "-deprecation", "-encoding", "utf8")

javacOptions ++= Seq("-Xlint:deprecation", "-encoding", "utf8")

mainClass in assembly := Some("org.kaloz.factorysimulation.Main")

assemblyJarName in assembly := "factorysimulation.jar"

libraryDependencies ++= Seq(
  "org.scalaz" %% "scalaz-core" % "7.1.1",
  "com.typesafe.akka" %% "akka-actor" % "2.3.11"
)

assemblyMergeStrategy in assembly := {
  case x if x.endsWith("UnusedStubClass.class") => MergeStrategy.first
  case PathList("com", "google", xs @ _*) => MergeStrategy.first
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}

// Test Dependencies

libraryDependencies ++= Seq(
  "org.specs2" %% "specs2-core" % "3.4" % "test",
  "org.specs2" %% "specs2-mock" % "3.4" % "test",
  "org.specs2" %% "specs2-junit" % "3.4" % "test",
  "com.typesafe.akka" %% "akka-testkit" % "2.3.11" % "test"
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
