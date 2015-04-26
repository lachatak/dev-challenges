package org.kaloz.bigdataaggregator

import scalaz.Scalaz._

object Domain {

  type Currency = String
  type Partner = String
  type Amount = BigDecimal
  type Rate = BigDecimal
  type TransactionFlow = Iterator[Transaction]
  type PartnerAmountSummary = Map[Partner, Amount]
  type ExchangeRate = ((Currency, Currency), Rate)
  type ExchangeRates = Map[(Currency, Currency), Rate]

  case class Transaction(partner: Partner, currency: Currency, amount: Amount = BigDecimal(0)) {

    def |~>(target: Currency)(implicit exchangeRates: ExchangeRates): Amount = target match {
      case `currency` => this.amount
      case _ => exchangeRates(currency, target) * this.amount
    }
  }

  trait Transactions {
    self: TransactionRepositoryComponent with ResultWriterComponent =>

    implicit lazy val exchangeRates = exchangeRateRepository.loadExchangeRates

    def loadTransactions: TransactionFlow = {
      transactionRepository.loadTransactions
    }

    def write(result: PartnerAmountSummary): Option[PartnerAmountSummary] = {
      resultWriter.write(result)
    }
  }

  trait TransactionInfo extends Transactions {
    self: TransactionRepositoryComponent with ResultWriterComponent =>

    def sumByPartnerAndCurrency(partner: Partner, currency: Currency): Amount =
      loadTransactions
        .filter(_.partner == partner)
        .foldLeft(BigDecimal(0))((acc, tr) => acc + (tr |~> currency))

    def sumByCurrency(currency: Currency): PartnerAmountSummary = {
      loadTransactions
        .foldLeft(Map.empty[Partner, Amount])((acc, tr) => acc |+| Map(tr.partner -> (tr |~> currency)))
    }
  }

  trait TransactionRepositoryComponent {

    def transactionRepository: TransactionRepository

    def exchangeRateRepository: ExchangeRateRepository

    trait TransactionRepository {
      def loadTransactions: TransactionFlow
    }

    trait ExchangeRateRepository {
      def loadExchangeRates: ExchangeRates
    }

  }

  trait ResultWriterComponent {

    def resultWriter: ResultWriter

    trait ResultWriter {
      def write(result: PartnerAmountSummary): Option[PartnerAmountSummary]
    }

  }

}
