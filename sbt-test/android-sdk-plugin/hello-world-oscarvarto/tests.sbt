import android.Keys._
import java.util.zip._
import java.io._
import android.BuildOutput._
import sys.process._

TaskKey[Unit]("check-dex") := {
  val p = ( SettingKey[Logger => com.android.builder.core.AndroidBuilder]("android-builder") in Android).value
  val layout = (projectLayout in Android).value
  val o = (outputLayout in Android).value
  val s = streams.value
  implicit val output = o
  val tools = p(s.log).getTargetInfo.getBuildTools.getLocation
  val dexdump = tools / "dexdump"
  val lines = Seq(
    dexdump.getAbsolutePath,
    (layout.dex / "classes.dex").getAbsolutePath).lineStream
  val hasMainActivity = lines exists { l =>
    l.trim.startsWith("Class descriptor") && l.trim.endsWith("MainActivity;'")}
  if (!hasMainActivity)
    sys.error("MainActivity not found\n" + (lines mkString "\n"))
}

TaskKey[Unit]("check-tr") := {
  val layout = (projectLayout in Android ).value
  val tr = layout.gen / "com" / "example" / "app" / "TR.scala"
  val lines = IO.readLines(tr)
  val expected =
    "final val hello = TypedLayout[android.widget.FrameLayout](R.layout.hello)"
  val hasTextView = lines exists (_.trim == expected)
  if (!hasTextView)
    sys.error("Could not find TR.test_textview\n" + (lines mkString "\n"))
}

TaskKey[Unit]("check-resource") := {
  val apk = ( sbt.Keys.`package` in Android ).value
  val zip = new ZipInputStream(new FileInputStream(apk))
  val names = Stream.continually(zip.getNextEntry()).takeWhile(_ != null).map {
    _.getName
  }
  val exists = names exists (_.endsWith("test.conf"))
  zip.close()
  if (!exists) {
    sys.error("Could not find test.conf\n" + (names mkString "\n"))
  }
}
