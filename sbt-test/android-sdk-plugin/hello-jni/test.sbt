TaskKey[Unit]("javah-finder") := {
  val headers = (baseDirectory.value ** "*.h").get
  streams.value.log.info("Headers: " + (headers mkString ","))
}
