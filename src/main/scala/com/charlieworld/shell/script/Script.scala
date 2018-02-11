package com.charlieworld.shell.script

import scala.annotation.tailrec
import scala.sys.process._
import scala.util.Try

/**
  * Writer Charlie Lee 
  * Created at 2018. 2. 11.
  */

case class Script[+A](run: Unit => ScriptResult[A])

object Script {

  type Cmds = Seq[String]

  def exec[A](cmd: String): Script[String] =
    exec(cmd.split(" "))

  def exec[A](cmd: Seq[String]): Script[String] = Script[String] { _ =>
    val stdout = new StringBuilder
    val stderr = new StringBuilder
    val logger = ProcessLogger(s => stdout.append(s + "\n"), s => stderr.append(s + "\n"))
    val cmdStr = cmd.mkString(" ")
    try {
      val exitCode = cmd.!(logger)
      val out = stdout.mkString
      val err = stderr.mkString
      val withoutTail =
        if (out.lastOption.contains('\n'))
          out.substring(0, out.length - 1)
        else
          out
      if (exitCode == 0)
        Success(Seq(cmdStr), withoutTail)
      else
        Failure(Seq(cmdStr), ScriptError(out, err))
    } catch {
      case _: Exception =>
        Failure(Seq(cmdStr), ScriptError(stdout.mkString, stderr.mkString))
    }
  }

  def env(name: String): Script[String] =
    Script.fromTry(Try(sys.env(name)))

  def fromTry[A](ta: Try[A]): Script[A] = ta match {
    case scala.util.Success(a) => Script.success(a)
    case scala.util.Failure(ex) => Script.failure(ex)
  }

  def success[A](a: => A): Script[A] =
    Script { _ => Success(Seq(), a) }

  def failure[A](ex: => Throwable): Script[A] =
    Script { _ => Failure(Seq(), ex) }

  def flatMap[A, B](fa: Script[A])(f: A => Script[B]): Script[B] = Script { _ =>
    fa.run() match {
      case Success(cmds, a) =>
        f(a).run() match {
          case Success(moreCmds, b) => Success(cmds ++ moreCmds, b)
          case Failure(moreCmds, ex) => Failure(cmds ++ moreCmds, ex)
        }
      case Failure(cmds, ex) =>
        Failure(cmds, ex)
    }
  }

  def tailRecM[A, B](a: A)(f: A => Script[Either[A, B]]): Script[B] = Script { _ =>
    @tailrec
    def go(acc: Cmds, x: A): ScriptResult[B] = {
      f(x).run() match {
        case Success(cmds, Right(b)) => Success(acc ++ cmds, b)
        case Success(cmds, Left(y)) => go(acc ++ cmds, y)
        case Failure(cmds, ex) => Failure(acc ++ cmds, ex)
      }
    }
    go(Seq(), a)
  }

  def recoverWith[A](fa: Script[A])(f: Throwable => Script[A]): Script[A] = Script { _ =>
    fa.run() match {
      case Success(cmds, a) =>
        Success(cmds, a)
      case Failure(cmds, ex) =>
        f(ex).run() match {
          case Success(cmds1, a) => Success(cmds ++ cmds1, a)
          case Failure(cmds1, ex1) => Failure(cmds ++ cmds1, ex1)
        }
    }
  }
}
