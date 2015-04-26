import sbtassembly.Plugin.AssemblyKeys._

name := "bigdataaggregator"

organization := "org.kaloz"

version := "1.0.0"

scalaVersion := "2.11.4"

scalacOptions ++= Seq("-unchecked", "-deprecation", "-encoding", "utf8")

javacOptions ++= Seq("-Xlint:deprecation", "-encoding", "utf8", "-XX:MaxPermSize=256M")

mainClass in assembly := Some("org.kaloz.bigdataaggregator.SparkMain")

jarName in assembly := "bigdataaggregator.jar"

val sharedMergeStrategy: (String => MergeStrategy) => String => MergeStrategy =
  old => {
    case x if x.startsWith("META-INF/ECLIPSEF.RSA") => MergeStrategy.last
    case x if x.startsWith("META-INF/mailcap") => MergeStrategy.last
    case x if x.endsWith("plugin.properties") => MergeStrategy.last
    case x if x.endsWith("mimetypes.default") => MergeStrategy.last
    case x => old(x)
  }

// Load Assembly Settings

assemblySettings

mergeStrategy in assembly <<= (mergeStrategy in assembly)(sharedMergeStrategy)

libraryDependencies ++= Seq(
  "org.scalaz" %% "scalaz-core" % "7.1.1",
  "org.apache.spark" %% "spark-core" % "1.3.1" excludeAll(
    ExclusionRule("commons-beanutils", "commons-beanutils-core"),
    ExclusionRule("commons-collections", "commons-collections"),
    ExclusionRule("commons-logging", "commons-logging"),
    ExclusionRule("org.slf4j", "slf4j-log4j12"),
    ExclusionRule("org.hamcrest", "hamcrest-core"),
    ExclusionRule("junit", "junit"),
    ExclusionRule("org.jboss.netty", "netty"),
    ExclusionRule("com.esotericsoftware.minlog", "minlog"),
    ExclusionRule("javax.activation", "activation"),
    ExclusionRule("com.google.guava", "guava"),
    ExclusionRule("org.apache.spark", "spark-network-shuffle_2.11"),
    ExclusionRule("org.apache.spark", "spark-network-common_2.11"),
    ExclusionRule("org.spark-project.spark", "unused"),
    ExclusionRule("org.apache.hadoop", "hadoop-yarn-api")
    )
)

// Test Dependencies

libraryDependencies ++= Seq(
  "org.specs2" %% "specs2-core" % "3.4" % "test",
  "org.specs2" %% "specs2-mock" % "3.4" % "test",
  "org.specs2" %% "specs2-junit" % "3.4" % "test"
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
