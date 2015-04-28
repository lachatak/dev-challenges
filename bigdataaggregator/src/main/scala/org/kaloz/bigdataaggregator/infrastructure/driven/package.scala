package org.kaloz.bigdataaggregator.infrastructure

package object driven {

  def benchmark[T](log: String, block: => T): T = {
    val startTime = System.currentTimeMillis()
    try {
      val r: T = block
      println(r.toString)
      r
    } finally {
      println(s"$log ${(System.currentTimeMillis() - startTime) / 1000.00}s")
    }
  }

}
