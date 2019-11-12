import android.Keys._
import Tests._

TaskKey[Unit]("check-for-nothing") := {
  val a = (apkFile in Android).value
  val found = findInArchive(a) (_ == "nothing.txt")
  if (!found) sys.error("nothing.txt not found in APK")
}

TaskKey[Seq[String]]("list-apk") := {
  val a = (apkFile in Android).value
  listArchive(a)
}
