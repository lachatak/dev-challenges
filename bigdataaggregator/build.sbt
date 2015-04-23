name := "bigdataaggregator"

organization := "org.kaloz"

version := "1.0.0"

scalaVersion := "2.11.4"

scalacOptions ++= Seq("-unchecked", "-deprecation", "-encoding", "utf8")

javacOptions ++= Seq("-Xlint:deprecation", "-encoding", "utf8", "-XX:MaxPermSize=256M")

mainClass in assembly := Some("org.kaloz.bigdataaggregator.HelloSpark")

libraryDependencies ++= Seq(
  "org.scalaz" %% "scalaz-core" % "7.1.1",
  "org.apache.spark" % "spark-core_2.11" % "1.2.0",
  "org.specs2" %% "specs2-core" % "3.4" % "test",
  "org.specs2" %% "specs2-mock" % "3.4" % "test",
  "org.specs2" %% "specs2-junit" % "3.4" % "test"
)

assemblyMergeStrategy in assembly := {
  case PathList("javax", "servlet", xs @ _*) => MergeStrategy.last
  case PathList("org", "apache", xs @ _*) => MergeStrategy.last
  case PathList("com", "esotericsoftware", xs @ _*) => MergeStrategy.last
  case "about.html" => MergeStrategy.rename
  case "mailcap" => MergeStrategy.rename
  case x => MergeStrategy.discard
}

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
