package org.kaloz

import scala.collection._


package object bigdataaggregator {

  val Opt = """(\S+)=(\S+)""".r

  def parseArgs(args: Array[String]): Map[String, String] = args.collect { case Opt(key, value) => key -> value}(breakOut)

  def benchmark[T](log: String, block: => T): T = {
    val startTime = System.currentTimeMillis()
    try {
      block
    } finally {
      println(s"$log ${(System.currentTimeMillis() - startTime) / 1000.00}s")
    }
  }

}
