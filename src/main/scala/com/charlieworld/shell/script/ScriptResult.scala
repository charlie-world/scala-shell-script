package com.charlieworld.shell.script

/**
  * Writer Charlie Lee 
  * Created at 2018. 2. 11.
  */
sealed trait ScriptResult[+A] {

  def cmds: Seq[String]

  def get: A
}

case class Success[A](cmds: Seq[String], get: A) extends ScriptResult[A]

case class Failure(cmds: Seq[String], error: Throwable) extends ScriptResult[Nothing] {
  override def get: Nothing = throw error
}
