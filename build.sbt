name := "Ubank"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "org.postgresql" % "postgresql" % "9.4-1200-jdbc41"
)     

play.Project.playScalaSettings
