resolvers += "typesafe" at "http://repo.typesafe.com/typesafe/releases"

libraryDependencies += "org.scala-sbt" %% "scripted-plugin" % sbtVersion.value

addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.8.0")
