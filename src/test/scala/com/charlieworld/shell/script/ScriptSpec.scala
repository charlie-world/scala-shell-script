package com.charlieworld.shell.script

import cats.Eq
import cats.implicits._
import cats.laws.discipline.{MonadErrorTests, MonadTests}
import org.scalacheck.Arbitrary
import org.scalatest.{FunSuite, Matchers}
import org.typelevel.discipline.scalatest.Discipline

/**
  * Writer Charlie Lee
  * Created at 2018. 2. 11.
  */
class ScriptSpec extends FunSuite with Matchers with Discipline {

  implicit def eqExec[T](implicit t: Eq[T]): Eq[Script[T]] = new Eq[Script[T]] {
    def eqv(x: Script[T], y: Script[T]): Boolean =
      x.run() == x.run()
  }

  implicit def eqThrowable: Eq[Throwable] = new Eq[Throwable] {
    def eqv(x: Throwable, y: Throwable): Boolean = {
      import java.io.{PrintWriter, StringWriter}
      val swX = new StringWriter
      val swY = new StringWriter
      x.printStackTrace(new PrintWriter(swX))
      y.printStackTrace(new PrintWriter(swY))
      swX.toString == swY.toString
    }
  }

  implicit def arbExec[T](implicit a: Arbitrary[T]): Arbitrary[Script[T]] =
    Arbitrary(a.arbitrary.map(v => Script.exec(s"echo $v").map(_ => v)))

  checkAll("Monad[Script]", {
    MonadTests[Script].monad[Int, Int, Int]
  })

  checkAll("MonadError[Script]", {
    MonadErrorTests[Script, Throwable].monadError[Int, Int, Int]
  })

  test("Including an output when it fails") {
    val errMsg = Script.exec("sbt notexists").run().asInstanceOf[Failure].error.getMessage
    println("An example error message")
    println(errMsg)
    errMsg.split("\n").exists(_ matches "Last stdout:") shouldBe true
  }
}
