TaskKey[Unit]("check-jar") := {
  val (inc, list) = (Android / dexInputs).value
  //Seq("jar", "tf", list(0).getAbsolutePath) !
  ()
}

