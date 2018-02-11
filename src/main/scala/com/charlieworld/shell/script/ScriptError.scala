package com.charlieworld.shell.script

/**
  * Writer Charlie Lee 
  * Created at 2018. 2. 11.
  */

case class ScriptError(lastStdout: String, lastStderr: String) extends Throwable {
  override def getMessage: String =
    s"""
       |Failed running Script.
       |-----
       |Last stdout:
       |$lastStdout
       |-----
       |Last stderr:
       |$lastStderr
    """.stripMargin
}
