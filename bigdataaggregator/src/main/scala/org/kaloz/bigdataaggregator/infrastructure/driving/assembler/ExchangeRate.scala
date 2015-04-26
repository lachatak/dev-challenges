package org.kaloz.bigdataaggregator.infrastructure.driving.assembler

import org.kaloz.bigdataaggregator.Domain.{ExchangeRate => DExchangeRate}

import scala.math.BigDecimal
import scala.util.Try

object ExchangeRate {
  def apply(er: String): Try[DExchangeRate] = {
    Try {
      val token = er.split(",").map(_.trim.toUpperCase)
      ((token(0), token(1)) -> BigDecimal(token(2)))
    }
  }
}
