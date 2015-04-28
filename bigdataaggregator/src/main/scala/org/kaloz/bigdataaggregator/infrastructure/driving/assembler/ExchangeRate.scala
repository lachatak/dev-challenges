package org.kaloz.bigdataaggregator.infrastructure.driving.assembler

import org.kaloz.bigdataaggregator.domain.Model.{ExchangeRate => DExchangeRate}

import scala.math.BigDecimal

object ExchangeRate {
  def apply(er: String): Option[DExchangeRate] = {
    try {
      val token = er.split(",").map(_.trim.toUpperCase)
      Some(((token(0), token(1)) -> BigDecimal(token(2))))
    }
    catch {
      case ex: Exception => None
    }
  }
}
