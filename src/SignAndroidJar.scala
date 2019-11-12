package android

import java.io.File
import java.net.URL

object SignAndroidJar {
  final class SignOption private[SignAndroidJar] (val toList: List[String], val signOnly: Boolean) {
    override def toString = toList.mkString(" ")
  }
  def keyStore(url: URL): SignOption = new SignOption("-keystore" :: url.toExternalForm :: Nil, true)
  def signedJar(p: File): SignOption = new SignOption("-signedjar" :: p.getAbsolutePath :: Nil, true)
  def verbose = new SignOption("-verbose" :: Nil, false)
  def sigFile(name: String) = new SignOption("-sigfile" :: name :: Nil, true)
  def storeType(t: String): SignOption = new SignOption("-storetype" :: t :: Nil, false)
  def provider(p: String) = new SignOption("-provider" :: p :: Nil, false)
  def providerName(p: String) = new SignOption("-providerName" :: p :: Nil, false)
  def storePassword(p: String): SignOption = new SignOption("-storepass" :: p :: Nil, true)
  def keyPassword(p: String): SignOption = new SignOption("-keypass" :: p :: Nil, true)

  private def VerifyOption = "-verify"

  /** Uses jarsigner to sign the given jar.  */
  def sign(jarPath: File, alias: String, options: Seq[SignOption])(fork: (String, List[String]) => Int) {
    require(!alias.trim.isEmpty, "Alias cannot be empty")
    val arguments = options.toList.flatMap(_.toList) ::: jarPath.getAbsolutePath :: alias :: Nil
    execute("signing", arguments)(fork)
  }
  /** Uses jarsigner to verify the given jar.*/
  def verify(jarPath: File, options: Seq[SignOption])(fork: (String, List[String]) => Int) {
    val arguments = options.filter(!_.signOnly).toList.flatMap(_.toList) ::: VerifyOption :: jarPath.getAbsolutePath :: Nil
    execute("verifying", arguments)(fork)
  }
  private def execute(action: String, arguments: List[String])(fork: (String, List[String]) => Int) {
    val exitCode = fork(CommandName, arguments)
    if (exitCode != 0)
      sys.error("Error " + action + " jar (exit code was " + exitCode + ".)")
  }

  private val CommandName = "jarsigner"
}