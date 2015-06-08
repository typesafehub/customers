name := "customers"

version := "0.1.0"

lazy val root = (project in file(".")).enablePlugins(ConductRPlugin, PlayScala)

scalaVersion := "2.11.6"

resolvers ++= Seq(
  "patriknw at bintray" at "http://dl.bintray.com/patriknw/maven",
  "typesafe-releases"   at "http://repo.typesafe.com/typesafe/maven-releases"
)

libraryDependencies ++= Seq(
  cache,
  "com.github.patriknw" %% "akka-data-replication" % "0.11",
  "com.typesafe.play" %% "play-slick" % "1.0.0",
  "org.postgresql" % "postgresql" % "9.4-1200-jdbc41",
  "com.typesafe.dynamicdatasource" %% "dynamic-data-source" % "0.1.0",
  "com.typesafe.conductr" %% "play24-conductr-bundle-lib" % "1.0.0"
)

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator

// ConductR

import ByteConversions._

BundleKeys.nrOfCpus := 1.0
BundleKeys.memory := 64.MiB
BundleKeys.diskSpace := 50.MiB

BundleKeys.system := "customer-cluster-1"

BundleKeys.endpoints := Map(
  "akka-remote" -> Endpoint("tcp"),
  "web" -> Endpoint("http", services = Set(URI("http://:9000")))
)
BundleKeys.startCommand += "-Dhttp.port=$WEB_BIND_PORT -Dhttp.address=$WEB_BIND_IP"

BundleKeys.configurationName := "customerdb"