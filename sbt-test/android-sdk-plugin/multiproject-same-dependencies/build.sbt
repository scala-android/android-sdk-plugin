javacOptions in Global ++= List("-source", "1.7", "-target", "1.7")

val sharedSettings = Seq(platformTarget := "android-23", showSdkProgress in Android := false, buildToolsVersion := Some("26.0.1"))

val b = project.settings(sharedSettings: _*).enablePlugins(AndroidLib)

val c = project.settings(sharedSettings: _*).enablePlugins(AndroidLib)

val d = project.settings(sharedSettings: _*).enablePlugins(AndroidLib)

val a = project.enablePlugins(AndroidApp).dependsOn(b,c,d).settings(sharedSettings: _*)

libraryDependencies in b += "com.android.support" % "appcompat-v7" % "23.1.1"

libraryDependencies in c += "com.android.support" % "appcompat-v7" % "23.1.1"

libraryDependencies in d += "com.android.support" % "appcompat-v7" % "23.1.1"

minSdkVersion in a := "7"

minSdkVersion in b := "7"

minSdkVersion in c := "7"

minSdkVersion in d := "7"
