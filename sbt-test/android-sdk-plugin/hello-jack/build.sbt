import android.Keys._

android.Plugin.androidBuild

platformTarget in Android := "android-21"

scalaVersion := "2.11.8"

useJack in Android := true

libraryDependencies += "io.argonaut" %% "argonaut" % "6.1"

buildToolsVersion in Android := Some("24.0.0-preview")