package org.kaloz.bigdataaggregator.infrastructure.driving.assembler

import org.junit.runner.RunWith
import org.kaloz.bigdataaggregator.domain.Model.{Transaction => DTransaction}
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class TransactionSpec extends Specification {

  "Transaction" should {

    "generate Some result if the transaction is parsable with lowercase currency" in {

      val result = Transaction("KRS,gbp,10.0")

      result must beSome(DTransaction("KRS", "GBP", BigDecimal(10.0)))
    }

    "generate Some result if the transaction is parsable" in {

      val result = Transaction("KRS,GBP,10.0")

      result must beSome(DTransaction("KRS", "GBP", BigDecimal(10.0)))
    }

    "generate a None if the transaction cannot be split" in {

      val result = Transaction("INVALID")

      result must beNone
    }

    "generate a None if the transaction amount is not number" in {

      val result = Transaction("KRS,GBP,INVALID")

      result must beNone
    }
  }
}
