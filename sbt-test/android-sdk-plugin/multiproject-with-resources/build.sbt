import sbt._
import sbt.Keys._
import android._
import android.Keys._
import Tests._

scalaVersion in ThisBuild := "2.10.2"

val core = Project("android-core", file("android-core")).settings(
    name := "Android Core",
    exportJars := true
)

lazy val androidScala = Project("android-main", file(".")).settings(
    libraryDependencies ++= Seq(
        "com.scalatags" %% "scalatags" % "0.2.4"
    ),
    javacOptions in Compile ++= Seq("-source", "1.6", "-target", "1.6"),
    showSdkProgress in Android := false
).enablePlugins(AndroidApp).dependsOn(core)

lazy val checkForNothing = TaskKey[Unit]("check-lib-for-nothing")

checkForNothing := {
    val j = (sbt.Keys.`package` in (core, Compile)).value
    val found = findInArchive(j) (_ == "nothing.txt")
    if (!found) sys.error("nothing.txt not found in library")
}
