package android

import java.io.File
import java.util.Properties

import sbt.io.{FileFilter, PathFinder, Using}

/**
  * @author pfnguyen
  */
private[android] trait TaskBase {

  def loadProperties(path: File): Properties = {
    val p = new Properties
    (PathFinder(path) * FileFilter.globFilter("*.properties")).get.foreach(Using.fileInputStream(_)(p.load))
    p
  }
}
