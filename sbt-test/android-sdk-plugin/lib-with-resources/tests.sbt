import android.Keys._
import Tests._

TaskKey[Unit]("check-for-properties") := {
  val a = (apkFile in Android).value
  val found = findInArchive(a) (_ == "com/example/lib/file.properties")
  if (!found) sys.error("Properties not found in APK\n" + listArchive(a))
}

TaskKey[Unit]("check-for-bin") := {
  val a = (apkFile in Android).value
  val found = findInArchive(a) (_ == "com/example/lib/library.bin")
  if (!found) sys.error("Bin file not found in APK")
}

TaskKey[Seq[String]]("list-apk") := {
  val a = (apkFile in Android).value
  listArchive(a)
}
