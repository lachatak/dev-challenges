package org.kaloz.bigdataaggregator.infrastructure.driving.assembler

import org.junit.runner.RunWith
import org.kaloz.bigdataaggregator.domain.Model.{Transaction => DTransaction}
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ExchangeRateSpec extends Specification {

  "ExchangeRate" should {

    "generate Some result if the rate is parsable with lowercase currency" in {

      val result = ExchangeRate("GBP,huf,10.0")

      result must beSome(("GBP", "HUF"), BigDecimal(10.0))
    }

    "generate Some result if the rate is parsable" in {

      val result = ExchangeRate("GBP,HUF,10.0")

      result must beSome(("GBP", "HUF"), BigDecimal(10.0))

    }

    "generate a None if the rate cannot be split" in {

      val result = Transaction("INVALID")

      result must beNone
    }

    "generate a None if the rate amount is not number" in {

      val result = Transaction("HUF,GBP,INVALID")

      result must beNone
    }
  }
}
