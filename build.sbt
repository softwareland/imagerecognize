name := "imagerecognize"
version := "1.0"
scalaVersion := "2.11.12"

resolvers += "MMLSpark Repo" at "https://mmlspark.azureedge.net/maven"

libraryDependencies ++= Seq(
  "org.apache.spark" % "spark-core_2.11" % "2.4.0",
  "org.apache.spark" % "spark-sql_2.11" % "2.4.0",
  "org.apache.spark" % "spark-streaming_2.11" % "2.4.0",
  "org.apache.spark" % "spark-mllib_2.11" % "2.4.0",
  "com.microsoft.ml.spark" %% "mmlspark" % "1.0.0-rc1",
  "org.vegas-viz" %% "vegas" % "0.3.11",
    "org.jmockit" % "jmockit" % "1.34" % "test"
)
assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}
