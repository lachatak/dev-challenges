package org.kaloz.bigdataaggregator.infrastructure.driving

import org.kaloz.bigdataaggregator.Domain._
import org.kaloz.bigdataaggregator.infrastructure.driving.assembler.Transaction

import scalaz.Scalaz._

trait FileTransactionRepositoryComponentImpl extends TransactionRepositoryComponent with ExchangeRateRepositoryComponent {

  implicit lazy val exchangeRates = exchangeRateRepository.loadExchangeRates

  class FileTransactionRepositoryImpl(transactionFileName: String) extends TransactionRepository with FromFile {
    override def loadTransactionsSumByCurrency(currency: Currency): Option[PartnerAmountSummary] =
      fromFile(transactionFileName)
        .map(Transaction(_))
        .foldLeft(Map.empty[Partner, Amount])((acc, tr) => acc |+| Map(tr.partner -> (tr |~> currency))) match {
        case m: Map[Partner, Amount] if m.isEmpty => None
        case m => m.some
      }

    override def loadTransactionsSumByPartnerAndCurrency(partner: Partner, currency: Currency): Amount =
      fromFile(transactionFileName)
        .map(Transaction(_))
        .filter(_.partner == partner)
        .foldLeft(BigDecimal(0))((acc, tr) => acc + (tr |~> currency))
  }

}
