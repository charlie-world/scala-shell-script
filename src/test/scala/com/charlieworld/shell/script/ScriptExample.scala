package com.charlieworld.shell.script

import org.scalatest.{FunSuite, Matchers}

/**
  * Writer Charlie Lee
  * Created at 2018. 2. 11.
  */
class ScriptExample extends FunSuite with Matchers {

  test("A single command script with success") {
    Script.exec("echo foo").run() shouldBe Success(Seq("echo foo"), "foo")
  }

  test("A single command script with failure") {
    val res = Script.exec("NOT_EXISTS_CMD").run()
    res.isInstanceOf[Failure] shouldBe true
    res.cmds shouldBe Seq("NOT_EXISTS_CMD")
  }

  test("Piping scripts") {
    val script = for {
      name <- Script.exec("echo foo")
      greeting <- Script.exec(s"echo hello, $name")
    } yield greeting
    script.run() shouldBe Success(Seq("echo foo", "echo hello, foo"), "hello, foo")
  }

  test("Piping scripts with handling errors") {
    val script = for {
      _ <- Script.exec("echo foo")
      name <- Script.exec("NOT_EXISTS_CMD").recover {
        case _: Throwable => "bar"
      }
      greeting <- Script.exec(s"echo hello, $name")
    } yield greeting
    script.run() shouldBe Success(Seq("echo foo", "NOT_EXISTS_CMD", "echo hello, bar"), "hello, bar")
  }

  test("Running a command with explicit arguments") {
    Script.exec(Seq("echo", "foo")).run() shouldBe Success(Seq("echo foo"), "foo")
  }
}
