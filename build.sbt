name := "customers"

version := "0.1.0"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.6"

resolvers ++= Seq(
  "patriknw at bintray" at "http://dl.bintray.com/patriknw/maven"
)

libraryDependencies ++= Seq(
  cache,
  "com.github.patriknw" %% "akka-data-replication" % "0.11",
  "com.typesafe.play" %% "play-slick" % "1.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "1.0.0",
  "org.postgresql" % "postgresql" % "9.4-1200-jdbc41",
  "com.typesafe.slick" %% "slick" % "3.0.1"
)

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
