package org.kaloz.bigdataaggregator.infrastructure.driving.stream

import org.junit.runner.RunWith
import org.specs2.matcher.ThrownExpectations
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Scope

@RunWith(classOf[JUnitRunner])
class StreamTransactionRepositoryComponentImplSpec extends Specification {

  "StreamTransactionRepositoryImpl" should {

    "parse valid input line and generate a valid grouped data" in new scope {

      doReturn(List("KRS,HUF,10.0").iterator).when(transactionRepository).fromStream("TRX")
      doReturn(List("HUF,GBP,1.0").iterator).when(transactionRepository).fromStream("EXC")

      val result = transactionRepository.loadTransactionsSumByCurrency("GBP")

      result must beSome(Map("KRS" -> BigDecimal(10.0)))
    }

    "parse valid input line with lowercase and generate a valid grouped data" in new scope {

      doReturn(List("KRS,huf,10.0").iterator).when(transactionRepository).fromStream("TRX")
      doReturn(List("huf,gbp,1.0").iterator).when(transactionRepository).fromStream("EXC")

      val result = transactionRepository.loadTransactionsSumByCurrency("GBP")

      result must beSome(Map("KRS" -> BigDecimal(10.0)))
    }

    "omit invalid transaction data and generate a valid grouped data" in new scope {

      doReturn(List("KRS,huf,10.0", "INVALID").iterator).when(transactionRepository).fromStream("TRX")
      doReturn(List("huf,gbp,1.0").iterator).when(transactionRepository).fromStream("EXC")

      val result = transactionRepository.loadTransactionsSumByCurrency("GBP")

      result must beSome(Map("KRS" -> BigDecimal(10.0)))
    }

    "omit invalid exchange rate and generate a valid grouped data" in new scope {

      doReturn(List("KRS,huf,10.0").iterator).when(transactionRepository).fromStream("TRX")
      doReturn(List("INVALID").iterator).when(transactionRepository).fromStream("EXC")

      val result = transactionRepository.loadTransactionsSumByCurrency("GBP")

      result must beSome(Map("KRS" -> BigDecimal(0)))
    }

    "parse valid input line and generate a valid amount" in new scope {

      doReturn(List("KRS,HUF,10.0").iterator).when(transactionRepository).fromStream("TRX")
      doReturn(List("HUF,GBP,1.0").iterator).when(transactionRepository).fromStream("EXC")

      val result = transactionRepository.loadTransactionsSumByPartnerAndCurrency("KRS", "GBP")

      result must beEqualTo(BigDecimal(10.0))
    }

    "parse valid input line with lowercase and generate a valid amount" in new scope {

      doReturn(List("KRS,huf,10.0").iterator).when(transactionRepository).fromStream("TRX")
      doReturn(List("huf,gbp,1.0").iterator).when(transactionRepository).fromStream("EXC")

      val result = transactionRepository.loadTransactionsSumByPartnerAndCurrency("KRS", "GBP")

      result must beEqualTo(BigDecimal(10.0))
    }

    "omit invalid transaction data and generate a valid amount" in new scope {

      doReturn(List("KRS,huf,10.0", "INVALID").iterator).when(transactionRepository).fromStream("TRX")
      doReturn(List("huf,gbp,1.0").iterator).when(transactionRepository).fromStream("EXC")

      val result = transactionRepository.loadTransactionsSumByPartnerAndCurrency("KRS", "GBP")

      result must beEqualTo(BigDecimal(10.0))
    }

    "omit invalid exchange rate and generate a valid amount" in new scope {

      doReturn(List("KRS,huf,10.0").iterator).when(transactionRepository).fromStream("TRX")
      doReturn(List("INVALID").iterator).when(transactionRepository).fromStream("EXC")

      val result = transactionRepository.loadTransactionsSumByPartnerAndCurrency("KRS", "GBP")

      result must beEqualTo(BigDecimal(0.0))
    }

    "generate wiki sample result Task1" in new scope {

      doReturn(List("Unlimited ltd.,GBP,200.5","Local plumber ltd.,GBP,50.2","Defence ltd.,USD,350.3","Local plumber ltd.,HUF,35000","Unlimited ltd.,CHF,157.0").iterator).when(transactionRepository).fromStream("TRX")
      doReturn(List("HUF,GBP,0.0025","USD,GBP,0.67","CHF,GBP,0.71","GBP,HUF,421.5").iterator).when(transactionRepository).fromStream("EXC")

      val result = transactionRepository.loadTransactionsSumByCurrency("GBP")

      result must beSome(Map("Unlimited ltd." -> BigDecimal(311.97), "Local plumber ltd." -> BigDecimal(137.7), "Defence ltd." -> BigDecimal(234.701)))
    }

    "generate wiki sample result Task2" in new scope {

      doReturn(List("Unlimited ltd.,GBP,200.5","Local plumber ltd.,GBP,50.2","Defence ltd.,USD,350.3","Local plumber ltd.,HUF,35000","Unlimited ltd.,CHF,157.0").iterator).when(transactionRepository).fromStream("TRX")
      doReturn(List("HUF,GBP,0.0025","USD,GBP,0.67","CHF,GBP,0.71","GBP,HUF,421.5").iterator).when(transactionRepository).fromStream("EXC")

      val result = transactionRepository.loadTransactionsSumByPartnerAndCurrency("Unlimited ltd.", "GBP")

      result must beEqualTo(BigDecimal(311.97))
    }
  }

  private trait scope extends Scope with Mockito with ThrownExpectations with StreamTransactionRepositoryComponentImpl {

    val transactionRepository = spy(new StreamTransactionRepositoryImpl("TRX", "EXC"))

  }

}
