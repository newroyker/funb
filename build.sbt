name := "funb"
organization := "roy"
version := "0.1"
scalaVersion := "2.11.12"

lazy val global = project
  .in(file("."))
  .aggregate(
    dp,
    lib
  )

lazy val lib = project
  .settings(
    name := "lib",
    scalaVersion := "2.11.12",
    libraryDependencies ++= Seq(
    	"org.apache.spark" %% "spark-sql" % "2.4.3",
    	"io.delta" %% "delta-core" % "0.3.0",
    	"org.scalatest" %% "scalatest" % "3.0.5" % Test
    )
  )

lazy val dp = project
  .settings(
    name := "dp",
    scalaVersion := "2.11.12",
    libraryDependencies ++= Seq(
    	"org.apache.spark" %% "spark-sql" % "2.4.3",
    	"io.delta" %% "delta-core" % "0.3.0",
      "com.databricks" % "dbutils-api_2.11" % "0.0.3"
    )
  )
  .dependsOn(
    lib
  )
