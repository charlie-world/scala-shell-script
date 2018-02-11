package com.charlieworld.shell.script

import cats.{Monad, MonadError}

/**
  * Writer Charlie Lee 
  * Created at 2018. 2. 11.
  */
trait ScriptInstances {

  implicit def catsStdInstancesForScript: Monad[Script] with MonadError[Script, Throwable] =
    new Monad[Script] with MonadError[Script, Throwable] {

      override def flatMap[A, B](fa: Script[A])(f: A => Script[B]): Script[B] =
        Script.flatMap(fa)(f)

      override def tailRecM[A, B](a: A)(f: A => Script[Either[A, B]]): Script[B] =
        Script.tailRecM(a)(f)

      override def pure[A](a: A): Script[A] =
        Script.success(a)

      override def raiseError[A](e: Throwable): Script[A] =
        Script.failure(e)

      override def handleErrorWith[A](fa: Script[A])(f: Throwable => Script[A]): Script[A] =
        Script.recoverWith(fa)(f)
    }
}

