package android

import java.io.{InputStreamReader, BufferedReader, StringWriter}

import argonaut._, Argonaut._

import scala.concurrent.Future
import scala.util.{Failure, Success}

object UpdateChecker {
  import scala.concurrent.ExecutionContext.Implicits.global
  type Result = (Set[String],String)
  type Callback[A] = Either[Throwable,Result] => A

  def apply[A](user: String, repo: String, name: String)(result: Callback[A]): Unit = {
    val bintray = new java.net.URL(
      s"https://api.bintray.com/packages/$user/$repo/$name")
    Future {
      val uc = bintray.openConnection()
      val in = new BufferedReader(new InputStreamReader(uc.getInputStream, "utf-8"))
      try {
        val sw = new StringWriter
        val buf = Array.ofDim[Char](8192)
        Stream.continually(in.read(buf, 0, 8192)) takeWhile (
          _ != -1) foreach (sw.write(buf, 0, _))
        sw.toString
      } finally {
        in.close()
      }
    } onComplete {
      case Success(json) =>
        val res: Either[Throwable, Result] = json.decode[PackageInfo] match {
          case Left(Left(str)) =>
            Left(new IllegalArgumentException(str))
          case Left(Right(cursorHistory)) =>
            Left(new IllegalArgumentException(cursorHistory._1))
          case Right(packageInfo) =>
            Right(packageInfo.versions.toSet -> packageInfo.version)
        }
        result(res)
      case Failure(t) => result(Left(t))
    }
  }

  implicit def PackageInfoCodecJson: CodecJson[PackageInfo] = casecodec3(
    PackageInfo.apply, PackageInfo.unapply)("name", "latest_version", "versions")

  case class PackageInfo(name: String, version: String, versions: List[String])
}