import android.Keys._

android.Plugin.androidBuild

platformTarget in Android := "android-21"

minSdkVersion in Android := "21"

targetSdkVersion in Android := "21"

name := "hello-multidex"

resolvers += bintray.Opts.resolver.jcenter

libraryDependencies ++= Seq(
  aar("com.google.android" % "multidex" % "0.1"),
  aar("com.google.android.gms" % "play-services" % "4.0.30"),
  aar("com.android.support" % "support-v4" % "20.0.0")
)

useProguard in Android := true

useProguardInDebug in Android := true

proguardScala in Android := true

dexMulti in Android := true

dexMinimizeMain in Android := true

dexMainClasses in Android := Seq(
  "com/example/app/MultidexApplication.class",
  "android/support/multidex/BuildConfig.class",
  "android/support/multidex/MultiDex$V14.class",
  "android/support/multidex/MultiDex$V19.class",
  "android/support/multidex/MultiDex$V4.class",
  "android/support/multidex/MultiDex.class",
  "android/support/multidex/MultiDexApplication.class",
  "android/support/multidex/MultiDexExtractor$1.class",
  "android/support/multidex/MultiDexExtractor.class",
  "android/support/multidex/ZipUtil$CentralDirectory.class",
  "android/support/multidex/ZipUtil.class"
)

useJack in Android := true

packagingOptions in Android := PackagingOptions(excludes = Seq(
  "META-INF/MANIFEST.MF",
  "META-INF/LICENSE.txt",
  "META-INF/LICENSE",
  "META-INF/NOTICE.txt",
  "META-INF/NOTICE"
))

javacOptions in Compile ++= Seq("-source", "1.6", "-target", "1.6")
