name := "backend"
 
version := "1.0" 
      
lazy val `backend` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
      
resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"
      
scalaVersion := "2.12.2"

libraryDependencies ++= Seq( jdbc , ehcache , ws , specs2 % Test , guice )

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )

libraryDependencies += "com.typesafe.play" %% "play-slick" % "4.0.0"
libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.24"

libraryDependencies += "com.google.code.gson" % "gson" % "2.8.5"

libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.5"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test"

