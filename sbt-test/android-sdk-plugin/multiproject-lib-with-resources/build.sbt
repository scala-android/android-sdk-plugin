import android.Keys._
import Tests._

scalaVersion in ThisBuild := "2.11.12"

// meta project
lazy val root = project.in(file(".")).aggregate(guidemate, geophon, guidemate_lib)

// android application project
lazy val guidemate = Project(id = "app", base = file("app")).enablePlugins(AndroidApp).dependsOn(guidemate_lib).settings(appSettings:_*)
lazy val geophon = Project(id = "app2", base = file("app2")).enablePlugins(AndroidApp).dependsOn(guidemate_lib).settings(appSettings :_*)

val guidemate_lib = Project(id = "lib",
  base = file("lib-with-resources"))
  .enablePlugins(AndroidLib)
  .settings(libraryDependencies ++= Seq(
    "org.scalatest" %% "scalatest" % "3.0.8" % "test",
    "com.pivotallabs" % "robolectric" % "1.1" % "test",
    "junit" % "junit" % "4.10" % "test",
    "commons-io" % "commons-io" % "2.1",
    "com.javadocmd" % "simplelatlng" % "1.0.0",
    "org.joda" % "joda-convert" % "1.2",
    "joda-time" % "joda-time" % "2.0",
    "commons-lang" % "commons-lang" % "2.6",
    "org.osmdroid" % "osmdroid-android" % "3.0.10",
    "org.slf4j" % "slf4j-simple" % "1.7.5"))

lazy val appSettings = List(
  showSdkProgress in Android := false,
  platformTarget in Android := "android-17",
  useProguard in Android := true,
  useProguardInDebug in Android := true,
  proguardScala in Android := true,
  proguardOptions in Android += "-dontwarn **",
  packagingOptions in Android := PackagingOptions(excludes = Seq(
    "META-INF/LICENSE.txt",
    "META-INF/NOTICE.txt")))

lazy val checkForProperties = TaskKey[Unit]("check-for-properties")

checkForProperties := {
  val a = (apkFile in (guidemate, Android)).value
  val found = findInArchive(a) (_ == "com/example/lib/file.properties")
  if (!found) sys.error("Properties not found in APK")
}

lazy val checkForBin = TaskKey[Unit]("check-for-bin")

checkForBin := {
  val a = (apkFile in (guidemate,Android)).value
  val found = findInArchive(a) (_ == "com/example/lib/library.bin")
  if (!found) sys.error("Bin file not found in APK")
}

lazy val listApk = TaskKey[Seq[String]]("list-apk")

listApk := {
  val a = (apkFile in (guidemate, Android)).value
  listArchive(a)
}

