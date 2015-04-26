package org.kaloz.bigdataaggregator.infrastructure.driving.assembler

import org.junit.runner.RunWith
import org.kaloz.bigdataaggregator.Domain.{Transaction => DTransaction}
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class TransactionSpec extends Specification {

  "Transaction" should {

    "generate a Transaction if the transaction is parsable with lowercase currency" in {

      val result = Transaction("KRS,gbp,10.0")

      result must beEqualTo(DTransaction("KRS", "GBP", BigDecimal(10.0)))
    }

    "generate a Transaction if the transaction is parsable" in {

      val result = Transaction("KRS,GBP,10.0")

      result must beEqualTo(DTransaction("KRS", "GBP", BigDecimal(10.0)))
    }
  }
}
