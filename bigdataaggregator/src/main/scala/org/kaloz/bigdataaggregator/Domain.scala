package org.kaloz.bigdataaggregator

import scalaz.Scalaz._

object Domain {

  type Currency = String
  type Partner = String
  type Amount = BigDecimal
  type Rate = BigDecimal
  type PartnerAmountSummary = Map[Partner, Amount]
  type ExchangeRate = ((Currency, Currency), Rate)
  type ExchangeRates = Map[(Currency, Currency), Rate]

  case class Transaction(partner: Partner, currency: Currency, amount: Amount = BigDecimal(0)) {

    def |~>(target: Currency)(implicit exchangeRates: ExchangeRates): Amount = target match {
      case `currency` => this.amount
      case _ => exchangeRates.getOrElse((currency, target), BigDecimal(0)) * this.amount
    }
  }

  class Transactions {
    self: TransactionRepositoryComponent with ResultWriterComponent =>

    def sumByCurrency(currency: Currency): Option[PartnerAmountSummary] = transactionRepository.loadTransactionsSumByCurrency(currency) >>= resultWriter.write

    def sumByPartnerAndCurrency(partner: Partner, currency: Currency): Amount = transactionRepository.loadTransactionsSumByPartnerAndCurrency(partner, currency)
  }

  trait TransactionRepositoryComponent {

    def transactionRepository: TransactionRepository

    trait TransactionRepository {
      def loadTransactionsSumByCurrency(currency: Currency): Option[PartnerAmountSummary]

      def loadTransactionsSumByPartnerAndCurrency(partner: Partner, currency: Currency): Amount
    }

  }

  trait ExchangeRateRepositoryComponent {

    def exchangeRateRepository: ExchangeRateRepository

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
