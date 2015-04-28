package org.kaloz.bigdataaggregator.infrastructure.driving

import org.junit.runner.RunWith
import org.kaloz.bigdataaggregator.Domain.Transaction
import org.specs2.matcher.ThrownExpectations
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Scope

@RunWith(classOf[JUnitRunner])
class FileTransactionRepositoryComponentImplSpec extends Specification {

//  "FileTransactionRepositoryImpl" should {
//
//    "parse valid input line and generate a Transaction" in new scope {
//
//      doReturn(List("KRS,GBP,10.0").iterator).when(transactionRepository).fromFile(anyString)
//
//      val result = transactionRepository.loadTransactionsSumByCurrency("GBP")
//
//      result.next() must beEqualTo(Transaction("KRS", "GBP", BigDecimal(10.0)))
//    }
//
//    "parse valid input line with lowercase and generate a Transaction" in new scope {
//
//      doReturn(List("KRS,gbp,10.0").iterator).when(transactionRepository).fromFile(anyString)
//
//      val result = transactionRepository.loadTransactionsSumByCurrency("GBP")
//
//      result.next() must beEqualTo(Transaction("KRS", "GBP", BigDecimal(10.0)))
//    }
//
//  }
//
//  "FileExchangeRateRepositoryImpl" should {
//
//    "parse valid input line and generate an ExchangeRate" in new scope {
//
//      doReturn(List("HUF,GBP,10.0").iterator).when(exchangeRateRepository).fromFile(anyString)
//
//      val result = exchangeRateRepository.loadExchangeRates
//
//      result must havePair(("HUF", "GBP") -> BigDecimal(10.0))
//    }
//
//    "parse valid input line with lowercase and generate an ExchangeRate" in new scope {
//
//      doReturn(List("huf,gbp,10.0").iterator).when(exchangeRateRepository).fromFile(anyString)
//
//      val result = exchangeRateRepository.loadExchangeRates
//
//      result must havePair(("HUF", "GBP") -> BigDecimal(10.0))
//    }
//  }
//
//  private trait scope extends Scope with Mockito with ThrownExpectations with FileTransactionRepositoryComponentImpl {
//
//    val transactionRepository = spy(new FileTransactionRepositoryImpl("NO_FILE"))
//
//    val exchangeRateRepository = spy(new FileExchangeRateRepositoryImpl("NO_FILE"))
//
//  }

}
