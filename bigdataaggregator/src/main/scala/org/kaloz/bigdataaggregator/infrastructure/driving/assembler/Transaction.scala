package org.kaloz.bigdataaggregator.infrastructure.driving.assembler

import org.kaloz.bigdataaggregator.domain.Model.{Transaction => DTransaction}

import scala.math.BigDecimal

object Transaction {
  def apply(tr: String): Option[DTransaction] = {
    try {
      val token = tr.split(",").map(_.trim)
      Some(DTransaction(token(0), token(1).toUpperCase, BigDecimal(token(2))))
    }
    catch {
      case ex: Exception => None
    }
  }
}
