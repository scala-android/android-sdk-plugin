import android.Keys._
import android.BuildOutput._

import sys.process._

TaskKey[Unit]("check-test-dex") := {
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
  val hasJunit = lines exists { l =>
    l.trim.startsWith("Class descriptor") && l.trim.endsWith("junit/Assert;'")}
  if (!hasJunit)
    sys.error("JUnit not found\n" + (lines mkString "\n"))
}
