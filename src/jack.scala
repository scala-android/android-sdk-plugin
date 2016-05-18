package android

import java.io.File
import java.util.Collections

import com.android.SdkConstants
import com.android.builder.core.AndroidBuilder
import com.android.ide.common.process.ProcessOutputHandler
import sbt._

import scala.language.postfixOps

object Jack {
  val JACK_EXT = ".jack"

  def jill(bld: AndroidBuilder
      , inputFiles: Seq[File]
      , baseDir: File
      , outDir: File
      , dexOptions: Aggregate.Dex
      , streams: sbt.Keys.TaskStreams): Seq[(File, File)] = {
    // TODO: convertLibaryToJackUsingApis
    outDir.mkdirs()
    def preJackBinary(binary: File, out: File) = AndroidBuilder.convertLibraryWithJillUsingCli(
      binary, out, dexOptions.toDexOptions(incremental = false), dexOptions.buildTools,
      true, SbtJavaProcessExecutor, SbtProcessOutputHandler(streams.log), logger(streams)
    )

    inputFiles.map { in =>
      val out = jillFileOutput(baseDir, outDir, in)
      if (!out.exists() || out.lastModified < in.lastModified) {
        streams.log.debug("Jill input: " + in.getAbsolutePath)
        streams.log.info("Jill converting " + in.getName)
        preJackBinary(in, out)
      }
      (in,out)
    }
  }

  def jillFileOutput(base: File, binPath: File, inFile: File) = {
    val rpath = inFile relativeTo base
    val f = rpath.fold(SdkLayout.predex)(_ => binPath) / jillFileName(inFile)
    f.getParentFile.mkdirs()
    f
  }

  def jillFileName(inFile: File) = {
    val n = inFile.getName
    val pos = n.lastIndexOf('.')

    val name = if (pos != -1) n.substring(0, pos) else n

    // add a hash of the original file path.
    val input = inFile.getAbsolutePath
    val hashCode = Hash.toHex(Hash(input))

    name + "-" + hashCode.toString + SdkConstants.DOT_JAR + JACK_EXT
  }

  def makeDex(bld: AndroidBuilder
      , dexOpts: Aggregate.Dex
      , predexed: Seq[(File, File)]
      , projectLayout: ProjectLayout
      , debug: Boolean
      , isLibrary: Boolean
      , minSdk: String
      , a: Aggregate.Proguard
      , ra: Aggregate.Retrolambda
      , pi: ProguardInputs
      , s: sbt.Keys.TaskStreams)(implicit outputLayout: BuildOutput.Converter): File = {
    val shouldProguard = !isLibrary && (if (debug) a.useProguardInDebug else a.useProguard)

    s.log.info(s"Jack with proguard enabled = $shouldProguard")
    val proguardCfg = if (shouldProguard) {
      Some(buildJackProguardConfig(a, ra, pi, projectLayout.proguardOut, s))
    } else None

    val proguardConfig = proguardCfg.map { cfg =>
      val proguardFile = projectLayout.proguardTxt
      IO.writeLines(proguardFile, cfg)
      proguardFile
    }
    val proguardMappingFile = proguardCfg.map(_ => file(Proguard.mappingsFile(projectLayout.proguardOut)))

    val dexOut = projectLayout.dex

    // we don't use it to compile so just skipping here
    val ecjOpts = blankFile(projectLayout.dex / "ecj.options")

    if (dexOpts.minimizeMain && dexOpts.mainClassesConfig.isFile) {
      s.log.warn("maindexclasses configuration is ignored by jack")
    }

    try {
     Jack.convertByteCode(bld,
                      dexOut,
                      dexOut / s"classes$JACK_EXT",
                      classpath = "",
                      jillProcessedLibraries = predexed.map(_._2),
                      ecjOptionFile = ecjOpts,
                      proguardConfig = proguardConfig,
                      mappingFile = proguardMappingFile,
                      jarJarRuleFiles = Nil,
                      multiDex = dexOpts.multi,
                      minSdkVersion = minSdk.toInt,
                      debugLog = false,
                      dexOpts.maxHeap,
                      SbtProcessOutputHandler(s.log))

      dexOut
    } catch {
      case any: Throwable =>
        proguardCfg.foreach(_.zipWithIndex.foreach { case (str, idx) => s.log.info(s"Proguard cfg #${idx + 1}: $str") })
        throw any
    }
  }


  private def buildJackProguardConfig(a: Aggregate.Proguard, ra: Aggregate.Retrolambda,
      inputs: ProguardInputs, b: File,  s: sbt.Keys.TaskStreams): Seq[String] = {
    Proguard.buildFullConfig(a, ra, inputs, b, s).
        filterNot(_.contains("-injars")). // jack does not support -injars at all - it's inputs are *.jack
        filterNot(_.contains("-outjars")) // by removing -outjars we ask jack to produce final *.jack combined output
  }

  private def blankFile(file: File) = {
    file.getParentFile.mkdirs()
    file.createNewFile()
    file
  }

  private def convertByteCode(bld: AndroidBuilder,
      dexOutputFolder: File,
      jackOutputFile: File,
      classpath: String,
      jillProcessedLibraries: Seq[File],
      ecjOptionFile: File,
      proguardConfig: Option[File],
      mappingFile: Option[File],
      jarJarRuleFiles: Seq[File],
      multiDex: Boolean,
      minSdkVersion: Int,
      debugLog: Boolean,
      javaMaxHeapSize: String,
      processOutputHandler: ProcessOutputHandler): Unit = {
    import scala.collection.JavaConversions._

    val proguardCfg = proguardConfig.map(Collections.singleton[File]).orNull
    val mappings = mappingFile.orNull

    bld.convertByteCodeWithJack(dexOutputFolder, jackOutputFile, classpath, jillProcessedLibraries, ecjOptionFile,
      proguardCfg, mappings, jarJarRuleFiles, "1.8", multiDex, minSdkVersion, debugLog, javaMaxHeapSize,
      processOutputHandler
    )
  }


  private def logger(streams: sbt.Keys.TaskStreams) = {
    val l = SbtILogger()
    l(streams.log)
    l
  }
}
