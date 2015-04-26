package org.kaloz.bigdataaggregator.infrastructure.driving.assembler

import org.kaloz.bigdataaggregator.Domain.{Transaction => DTransaction}

import scala.math.BigDecimal

object Transaction {
  def apply(tr: String): DTransaction = {
    val token = tr.split(",").map(_.trim.toUpperCase)
    DTransaction(token(0), token(1), BigDecimal(token(2)))
  }
}
