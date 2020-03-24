

name := "imagerecognize"
version := "1.0"
scalaVersion := "2.11.12"

//unmanagedJars in Compile += file("lib/mmlspark-assembly-1.0.0-rc1-75-99795bc3-SNAPSHOT.jar")

resolvers += "MMLSpark Repo" at "https://mmlspark.azureedge.net/maven"

libraryDependencies ++= Seq(
  "com.microsoft.ml.spark" %% "mmlspark" % "1.0.0-rc1",
  "org.vegas-viz" %% "vegas" % "0.3.11"
)
assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}
