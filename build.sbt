name := "play-tech-demo"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  "com.restfb" % "restfb" % "1.6.12"
)     

play.Project.playJavaSettings
