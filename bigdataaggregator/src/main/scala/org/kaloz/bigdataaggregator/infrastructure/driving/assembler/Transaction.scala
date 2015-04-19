package org.kaloz.bigdataaggregator.infrastructure.driving.assembler

import org.kaloz.bigdataaggregator.Domain.{Transaction => DTransaction}

import scala.math.BigDecimal
import scala.util.Try

object Transaction {
  def apply(tr: String): Try[DTransaction] = {
    Try {
      val token = tr.split(",").map(_.trim)
      DTransaction(token(0), token(1).toUpperCase, BigDecimal(token(2)))
    }
  }
}
