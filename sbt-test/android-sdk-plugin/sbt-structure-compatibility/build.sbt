import org.jetbrains.sbt.StructureKeys._

enablePlugins(AndroidApp)

javacOptions in Compile ++= "-source" :: "1.7" :: "-target" :: "1.7" :: Nil

sbtStructureOutputFile in Global := Some(baseDirectory.value / "structure.xml")

sbtStructureOptions in Global := "download prettyPrint"
