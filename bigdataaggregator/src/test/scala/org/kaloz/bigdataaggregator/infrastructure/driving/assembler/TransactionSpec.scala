package org.kaloz.bigdataaggregator.infrastructure.driving.assembler

import org.junit.runner.RunWith
import org.kaloz.bigdataaggregator.Domain.{Transaction => DTransaction}
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner

import scala.util.{Failure, Success}

@RunWith(classOf[JUnitRunner])
class TransactionSpec extends Specification {

  "Transaction" should {

    "generate a Success if the transaction is parsable" in {

      val result = Transaction("KRS,GBP,10.0")

      result must beSuccessfulTry(DTransaction("KRS", "GBP", BigDecimal(10.0)))
    }

    "generate a Failure with ArrayIndexOutOfBoundsException if the transaction cannot be split" in {

      val result = Transaction("INVALID")

      result must beFailedTry.withThrowable[ArrayIndexOutOfBoundsException]
    }

    "generate a Failure with ArrayIndexOutOfBoundsException if the transaction amount is not number" in {

      val result = Transaction("KRS,GBP,INVALID")

      result must beFailedTry.withThrowable[NumberFormatException]
    }
  }
}
