import sbt.Keys.resolvers

name := "imagerecognize"
version := "1.0"
scalaVersion := "2.11.12"

resolvers += "MMLSpark Repo" at "https://mmlspark.azureedge.net/maven"

libraryDependencies ++= Seq(
"org.apache.spark" % "spark-core_2.11" % "2.4.0",
"org.apache.spark" % "spark-sql_2.11" % "2.4.0",
"org.apache.spark" % "spark-streaming_2.11" % "2.4.0",
"org.apache.spark" % "spark-mllib_2.11" % "2.4.0",
"org.jmockit" % "jmockit" % "1.34" % "test", 
  "com.microsoft.ml.spark" %% "mmlspark" % "1.0.0-rc1",
 "org.vegas-viz" %% "vegas" % "0.3.11"
)