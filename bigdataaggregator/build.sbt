name := "bigdataaggregator"

organization := "org.kaloz"

version := "1.0.0"

scalaVersion := "2.11.4"

scalacOptions ++= Seq("-unchecked", "-deprecation", "-encoding", "utf8")

javacOptions ++= Seq("-Xlint:deprecation", "-encoding", "utf8", "-XX:MaxPermSize=256M")

mainClass in assembly := Some("org.kaloz.bigdataaggregator.SparkMain")

assemblyJarName in assembly := "bigdataaggregator.jar"

libraryDependencies ++= Seq(
  "org.scalaz" %% "scalaz-core" % "7.1.1",
  "org.apache.spark" %% "spark-core" % "1.3.1" excludeAll(
    ExclusionRule("commons-beanutils", "commons-beanutils-core"),
    ExclusionRule("commons-collections", "commons-collections"),
    ExclusionRule("commons-logging", "commons-logging"),
    ExclusionRule("com.esotericsoftware.minlog", "minlog"),
    ExclusionRule("org.apache.hadoop", "hadoop-yarn-api")
    )
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
  "org.specs2" %% "specs2-junit" % "3.4" % "test"
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
