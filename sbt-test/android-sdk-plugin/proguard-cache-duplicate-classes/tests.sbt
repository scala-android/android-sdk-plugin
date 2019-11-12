import android.Keys._
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
  val hasAbstractSuite = lines exists { l =>
    l.trim.startsWith("Class descriptor") && l.trim.endsWith("AbstractSuite$class;'")}
  if (!hasAbstractSuite)
    sys.error("AbstractSuite not found")
}

TaskKey[Unit]("check-cache") := {
  import java.io._
  import java.util.zip._
  val c = (TaskKey[ProguardInputs]("proguard-inputs") in Android).value
  val in = new ZipInputStream(new FileInputStream(c.proguardCache.get))
  val suite = Stream.continually(in.getNextEntry) takeWhile (
    _ != null) exists { e => e.getName.endsWith("AbstractSuite$class.class") }
  if (!suite) sys.error("AbstractSuite not found in cache")
  in.close()
}
