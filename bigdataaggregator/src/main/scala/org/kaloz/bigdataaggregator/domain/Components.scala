package org.kaloz.bigdataaggregator.domain

import org.kaloz.bigdataaggregator.domain.Model.{Amount, Currency, Partner, PartnerAmountSummary}

trait TransactionRepositoryComponent {

  def transactionRepository: TransactionRepository

  trait TransactionRepository {
    def loadTransactionsSumByCurrency(currency: Currency): Option[PartnerAmountSummary]

    def loadTransactionsSumByPartnerAndCurrency(partner: Partner, currency: Currency): Amount
  }

}

trait ResultWriterComponent {

  def resultWriter: ResultWriter

  trait ResultWriter {
    def write(result: PartnerAmountSummary): Option[PartnerAmountSummary]
  }

}

