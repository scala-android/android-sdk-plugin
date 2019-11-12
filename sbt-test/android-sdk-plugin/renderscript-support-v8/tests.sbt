import android.Keys._
import Tests._

TaskKey[Unit]("check-target-api") := {
  val targetAPI = (rsTargetApi in Android).value
  if (targetAPI != "18")
    sys.error("Renderscript targetApi not equal to 18: " + targetAPI)
}

TaskKey[Unit]("check-support-mode") := {
  if (!(rsSupportMode in Android).value)
    sys.error("Renderscript support mode was not set from project.properties")
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

val jniLibs = Seq(
  "x86/librs.invert.so",
  "x86/librsjni.so",
  "x86/libRSSupport.so",
  "armeabi-v7a/librs.invert.so",
  "armeabi-v7a/librsjni.so",
  "armeabi-v7a/libRSSupport.so"
)

def checkLibsInArchive(a: File, libs: Seq[String]) = {
  val entries = listArchive(a).toSet
  libs foreach { lib =>
    if (!entries.contains(lib)) sys.error(s"Library: $lib missing in archive: $a")
  }
}

//TaskKey[Unit]("check-aar-for-libs") := {
//  val a = (packageAar in Android).value
//  checkLibsInArchive(a, "libs/renderscript-v8.jar" +: (jniLibs.map("jni/" + _)))
//}

TaskKey[Unit]("check-apk-for-libs") := {
  val a = (apkFile in Android).value
  checkLibsInArchive(a, jniLibs.map("lib/" + _))
}

TaskKey[Seq[String]]("list-apk") := {
  val a = (apkFile in Android).value
  listArchive(a)
}

//TaskKey[Seq[String]]("list-aar") := (packageAar in Android) map { a =>
//  listArchive(a)
//}
