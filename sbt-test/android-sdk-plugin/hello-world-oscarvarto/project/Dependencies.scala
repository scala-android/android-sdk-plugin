import sbt._

object Settings extends AutoPlugin {
  object autoImport {
    object Version {
      val scala = "2.10.2"
    }

    object Dependency {
      object Compile {
        val shapeless = "com.chuusai" % "shapeless" % "2.0.0-M1" cross CrossVersion.full
        val scalazCore = "org.scalaz" %% "scalaz-core" % "7.0.3"
      }

      object Test {
        val specs2 = "org.specs2" %% "specs2" % "2.2" % "test"
      }
    }

  }
}
