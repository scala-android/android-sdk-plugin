enablePlugins(AndroidApp)

scalaVersion := "2.11.11"

javacOptions in Compile ++= "-source" :: "1.7" :: "-target" :: "1.7" :: Nil

minSdkVersion := "8"

showSdkProgress := false
