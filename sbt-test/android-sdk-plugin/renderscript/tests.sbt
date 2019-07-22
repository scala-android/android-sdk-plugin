import android.Keys._
import Tests._

TaskKey[Unit]("check-target-api-equals-min") := {
  val targetAPI = (rsTargetApi in Android).value
  if (targetAPI != "17")
    sys.error("Renderscript targetApi: " + targetAPI + " not equal to minSdkVersion")
}

TaskKey[Unit]("check-apk-for-resource") := {
  val a = (apkFile in Android).value
  val found = findInArchive(a) (_ == "res/raw/invert.bc")
  if (!found) sys.error("Renderscript resource not found in APK\n" + listArchive(a))
}

//TaskKey[Unit]("check-aar-for-resource") := (packageAar in Android) map { a =>
//  val found = findInArchive(a) (_ == "res/raw/invert.bc")
//  if (!found) sys.error("Renderscript resource not found in Aar\n" + listArchive(a))
//}

//TaskKey[Unit]("check-aar-no-libs") := (packageAar in Android) map { a =>
//  val found = findInArchive(a) (_.contains("libs"))
//  if (found) sys.error("Some library was included in aar\n" + listArchive(a))
//}

TaskKey[Seq[String]]("list-apk") := {
  val a = (apkFile in Android).value
  listArchive(a)
}

TaskKey[Seq[String]]("list-apk") := {
  val a = (apkFile in Android).value
  listArchive(a)
}
