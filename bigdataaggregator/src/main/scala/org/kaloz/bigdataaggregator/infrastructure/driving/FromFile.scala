package org.kaloz.bigdataaggregator.infrastructure.driving

import scala.io.Source

trait FromFile {
  def fromFile(transactionFileName: String): Iterator[String] = {
    Source
      .fromFile(transactionFileName)
      .getLines()
  }
}
