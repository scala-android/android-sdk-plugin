package android

import sbt.{Configuration, Def}
import Def.Setting
import scala.language.experimental.macros

package object dsl {
  def list[A](body: Seq[A]): List[A] = macro dsl.Macros.listImplN[A]
  def list[A](body: A): List[A]      = macro dsl.Macros.listImpl1[A]

  def inProject(project: String)(ss: Setting[_]*): Seq[Setting[_]] =
    inProject(sbt.ProjectRef(sbt.file(".").getCanonicalFile, project))(ss:_*)
  def inProject(project: sbt.ProjectRef)(ss: Setting[_]*): Seq[Setting[_]] =
    ss map VariantSettings.fixProjectScope(project)
  private def stringFlags(key: sbt.TaskKey[Seq[String]], ss: Seq[String]) = key ++= ss
  private def stringFlags(key: sbt.TaskKey[Seq[String]], config: Configuration, ss: Seq[String]) =
    key in config ++= ss
  def javacFlags(opts: String*) = stringFlags(sbt.Keys.javacOptions, opts)
  def javacFlags(config: Configuration)(opts: String*) =
    stringFlags(sbt.Keys.javacOptions, config, opts)
  def scalacFlags(opts: String*) = stringFlags(sbt.Keys.scalacOptions, opts)
  def scalacFlags(config: Configuration)(opts: String*) =
    stringFlags(sbt.Keys.scalacOptions, config, opts)

  def dexMainClassList(classes: String*) = Keys.dexMainClassesConfig := {
    val layout = Keys.projectLayout.value
    implicit val out = Keys.outputLayout.value
    sbt.IO.writeLines(layout.maindexlistTxt, classes)
    layout.maindexlistTxt
  }
}
package dsl {
private[android] object Macros {
  import scala.reflect.macros.blackbox.Context

  def listImplN[A](c: Context)(body: c.Expr[Seq[A]])(implicit ev: c.WeakTypeTag[A]): c.Expr[List[A]] = {
    import c.universe._
    val xs = body.tree.children
    if (xs.isEmpty)
      c.Expr[List[A]](Apply(Select(body.tree, TermName("toList")), Nil))
    else
      commonImpl(c)(body)
  }

  def listImpl1[A](c: Context)
                 (body: c.Expr[A])
                 (implicit ev: c.WeakTypeTag[A]): c.Expr[List[A]] = {
    import c.universe._
    val xs = body.tree.children
    if (xs.isEmpty)
      c.Expr[List[A]](Apply(Ident(TermName("List")), body.tree :: Nil))
    else
      commonImpl(c)(body)
  }

  def commonImpl[A](c: Context)(body: c.Expr[_])(implicit ev: c.WeakTypeTag[A]): c.Expr[List[A]] = {
    import c.universe._
    val seqA = c.weakTypeOf[Seq[A]]
    c.Expr[List[A]](body.tree.children.reduce { (a,ch) =>
        val acc = if (a.tpe != null && a.tpe <:< ev.tpe) {
          Apply(Ident(TermName("List")), a :: Nil)
        } else a
        if (ch.tpe <:< seqA)
          Apply(Select(acc, TermName("$plus$plus")), List(ch))
        else if (ch.tpe <:< ev.tpe)
          Apply(Select(acc, TermName("$colon$plus")), List(ch))
        else c.abort(ch.pos, s"Unexpected type: ${ch.tpe}, needed ${ev.tpe}")
      })
  }

}
}
