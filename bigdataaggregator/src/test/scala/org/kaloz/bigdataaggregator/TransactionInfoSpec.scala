package org.kaloz.bigdataaggregator

import org.junit.runner.RunWith
import org.kaloz.bigdataaggregator.Domain._
import org.specs2.matcher.ThrownExpectations
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Scope

import scala.util.{Failure, Success}
import scalaz.Scalaz._

@RunWith(classOf[JUnitRunner])
class TransactionInfoSpec extends Specification {

  "TransactionInfo" should {

    "give back correct sum amount for a partner and currency if TransactionFlow and ExchangeRates are provided" in new scope {

      exchangeRateRepository.loadExchangeRates returns Map(("CHF", "GBP") -> BigDecimal(2))
      transactionRepository.loadTransactions returns (Transaction("OTHER", "CHF", BigDecimal(1)) :: List.fill(5)(Transaction("KRS", "CHF", BigDecimal(1)))).iterator

      val result = sumByPartnerAndCurrency("KRS", "GBP")

      result mustEqual Some(10)
    }

    "give back None sum amount for a partner and currency if TransactionFlow is empty and ExchangeRates are provided" in new scope {

      exchangeRateRepository.loadExchangeRates returns Map(("CHF", "GBP") -> BigDecimal(2))
      transactionRepository.loadTransactions returns List.empty.iterator

      val result = sumByPartnerAndCurrency("KRS", "GBP")

      result mustEqual None
    }

    "give back correct grouping by partner for a given currency if TransactionFlow and ExchangeRates are provided" in new scope {

      exchangeRateRepository.loadExchangeRates returns Map(("CHF", "GBP") -> BigDecimal(2))
      transactionRepository.loadTransactions returns List(Transaction("OTHER", "CHF", BigDecimal(1)), Transaction("KRS", "CHF", BigDecimal(1))).iterator

      val result = sumByCurrency("GBP")

      result must beSome(List(("KRS", BigDecimal(2)), ("OTHER", BigDecimal(2))))
      there was one(resultWriter).write(any[PartnerAmountSummary])
    }

    "give back None grouping by partner for a given currency if TransactionFlow is empty and ExchangeRates are provided" in new scope {

      exchangeRateRepository.loadExchangeRates returns Map(("CHF", "GBP") -> BigDecimal(2))
      transactionRepository.loadTransactions returns List.empty.iterator

      val result = sumByCurrency("GBP")

      result must beNone
      there was no(resultWriter).write(any[PartnerAmountSummary])
    }
  }

  private trait scope extends Scope with ThrownExpectations with Mockito with TransactionInfo with TransactionRepositoryComponent with ResultWriterComponent {

    val transactionRepository = mock[TransactionRepository]

    val exchangeRateRepository = mock[ExchangeRateRepository]

    val resultWriter = mock[ResultWriter]

    resultWriter.write(any[PartnerAmountSummary]) answers (_.asInstanceOf[PartnerAmountSummary].some)
  }
}

