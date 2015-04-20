package org.kaloz.bigdataaggregator

/**
 * Created by krisztian.lachata on 11/04/15.
 */
object ReaderMonad extends App {

  case class Test(g: String => Int) {
    def apply(s: String) = g(s)
  }

  println(for{
    a <- 'a' to 'z'
    b <- 'a' to 'z'
  } yield s"$a$b$a")

  println(Test(_.size)("lofasz"))
}
