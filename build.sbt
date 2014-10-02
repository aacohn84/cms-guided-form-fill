name := """cms-guided-form-fill"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  "mysql" % "mysql-connector-java" % "5.1.33",
  "org.apache.pdfbox" % "pdfbox" % "1.8.6",
  javaJdbc,
  javaEbean,
  cache,
  javaWs
)
