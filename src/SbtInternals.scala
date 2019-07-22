package sbt

import sbt.Keys._

object SbtInternals {
  def reporter: Def.Initialize[Task[xsbti.Reporter]] = Def.task {
    (Compile / compile / compilerReporter).value
  }
}
