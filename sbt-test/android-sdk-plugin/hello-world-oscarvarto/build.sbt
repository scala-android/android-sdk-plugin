import android.Keys._
import scalariform.formatter.preferences._

enablePlugins(AndroidApp)

platformTarget in Android := "android-17"

name := "hello-world"

javacOptions in Compile ++= Seq("-source", "1.6", "-target", "1.6")

showSdkProgress in Android := false

lazy val androidScala = Project(
  "tims-scala-android",
  file(".")).settings(commonSettings ++ Seq(
    libraryDependencies ++= Seq()
  )
)

scalariformPreferences := scalariformPreferences.value
  .setPreference(RewriteArrowSymbols, true)
  .setPreference(AlignParameters, true)
  .setPreference(AlignSingleLineCaseStatements, true)
  .setPreference(PlaceScaladocAsterisksBeneathSecondAsterisk, true)
  .setPreference(CompactControlReadability, false)

def commonSettings = Seq(
    organization := "com.optrak",
    scalaVersion := Version.scala,
    scalacOptions ++= Seq(
      "-unchecked",
      "-deprecation",
      "-Xlint",
      "-language:_",
      "-encoding", "UTF-8"
    ),
    libraryDependencies ++= Seq(
      Dependency.Compile.shapeless,
      Dependency.Compile.scalazCore
    )
  )
