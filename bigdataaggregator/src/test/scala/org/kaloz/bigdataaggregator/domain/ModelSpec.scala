package org.kaloz.bigdataaggregator.domain

import org.junit.runner.RunWith
import org.kaloz.bigdataaggregator.domain.Model.{Currency, Rate, Transaction}
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ModelSpec extends Specification {

  "Transaction" should {

    "convert transaction amount to a given currency if the currency exists in exchange rates" in {

      implicit val exchangeRates = Map(("HUF", "GBP") -> BigDecimal(1))

      val trx = Transaction("KRS", "HUF", 1)
      val result = trx |~> "GBP"

      result mustEqual BigDecimal(1)
    }

    "convert transaction amount 0 if the currency does not exist in exchange rates" in {

      implicit val exchangeRates = Map(("HUF", "GBP") -> BigDecimal(1))

      val trx = Transaction("KRS", "CHF", 1)
      val result = trx |~> "GBP"

      result mustEqual BigDecimal(0)
    }

    "give back original amount if the target currency is the same as the transaction currency" in {

      implicit val exchangeRates = Map.empty[(Currency, Currency), Rate]

      val trx = Transaction("KRS", "GBP", 3)
      val result = trx |~> "GBP"

      result mustEqual BigDecimal(3)
    }
  }

}

