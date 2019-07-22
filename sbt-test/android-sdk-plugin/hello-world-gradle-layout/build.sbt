import android.Keys._

enablePlugins(AndroidApp)

platformTarget in Android := "android-17"

name := "hello-world"

javacOptions ++= Seq("-source", "1.6", "-target", "1.6")

showSdkProgress in Android := false
