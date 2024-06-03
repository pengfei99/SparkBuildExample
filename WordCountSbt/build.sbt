ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.12.19"
ThisBuild / organization := "org.casd"
ThisBuild / description := "sbt build spark jar for spark submit"

lazy val root = (project in file("."))
  .settings(
    name := "WordCount",
    version := "1.0",
    Compile / mainClass := Some("org.casd.wordcount.WordCountMain"),
    assembly / mainClass := Some("org.casd.wordcount.WordCountMain")
  )

val sparkVersion = "3.5.1"
libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion % "provided",
  "org.apache.spark" %% "spark-sql" % sparkVersion % "provided"
)

fork := true

assembly / assemblyJarName := "spark-wordcount.jar"

assembly / assemblyMergeStrategy  := {
  case PathList("META-INF", "services", _*) => MergeStrategy.concat
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case _ => MergeStrategy.first
}