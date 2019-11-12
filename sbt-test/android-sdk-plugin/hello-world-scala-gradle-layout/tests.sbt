import android.Keys._
import android.BuildOutput._

import sys.process._

TaskKey[Unit]("check-dex") := {
      val p = (SettingKey[Logger => com.android.builder.core.AndroidBuilder]("android-builder") in Android).value
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
  val layout = (projectLayout in Android).value
  val tr = layout.gen / "com" / "example" / "app" / "TR.scala"
  val lines = IO.readLines(tr)
  val expected =
    "final val hello = TypedLayout[android.widget.FrameLayout](R.layout.hello)"
  val hasTextView = lines exists (_.trim == expected)
  if (!hasTextView)
    sys.error("Could not find TR.hello\n" + (lines mkString "\n"))
}
