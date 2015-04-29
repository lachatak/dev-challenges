package org.kaloz.bigdataaggregator.infrastructure.driving.stream

import org.kaloz.bigdataaggregator.domain.Model._
import org.kaloz.bigdataaggregator.domain.TransactionRepositoryComponent
import org.kaloz.bigdataaggregator.infrastructure.driving.assembler.{ExchangeRate, Transaction}

import scala.io.Source
import scalaz.Scalaz._

trait StreamTransactionRepositoryComponentImpl extends TransactionRepositoryComponent {

  class StreamTransactionRepositoryImpl(transactionFileName: String, exchangeFileName: String) extends TransactionRepository {

    implicit lazy val exchangeRates: ExchangeRates =
      fromStream(exchangeFileName)
        .flatMap(ExchangeRate(_))
        .foldLeft(Map.empty[(Currency, Currency), Amount])(_ + _)

    override def loadTransactionsSumByCurrency(currency: Currency): Option[PartnerAmountSummary] =
      fromStream(transactionFileName)
        .flatMap(Transaction(_))
        .foldLeft(Map.empty[Partner, Amount])((acc, tr) => acc |+| Map(tr.partner -> (tr |~> currency))) match {
        case m: Map[Partner, Amount] if m.isEmpty => None
        case m => m.some
      }

    override def loadTransactionsSumByPartnerAndCurrency(partner: Partner, currency: Currency): Amount =
      fromStream(transactionFileName)
        .flatMap(Transaction(_))
        .filter(_.partner == partner)
        .foldLeft(BigDecimal(0))((acc, tr) => acc + (tr |~> currency))

    def fromStream(transactionFileName: String): Iterator[String] = {
      Source
        .fromFile(transactionFileName)
        .getLines()
    }
  }

}