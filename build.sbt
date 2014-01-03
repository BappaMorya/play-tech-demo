name := "play-tech-demo"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  "com.restfb" % "restfb" % "1.6.12",
  "com.google.guava" % "guava" % "15.0"
)     

play.Project.playJavaSettings
