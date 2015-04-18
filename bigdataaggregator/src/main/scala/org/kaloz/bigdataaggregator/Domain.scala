package org.kaloz.bigdataaggregator

import scalaz.Scalaz._

object Domain {

  type Currency = String
  type Partner = String
  type Amount = BigDecimal
  type TransactionFlow = Iterator[Transaction]
  type PartnerAmountSummary = Map[Partner, Amount]
  type ExchangeRates = Map[(Currency, Currency), Amount]

  case class Transaction(partner: Partner, currency: Currency, amount: Amount = BigDecimal(0)) {

    def add(addition: Amount) = copy(amount = amount + addition)

    def |~>(target: Currency)(implicit exchangeRates: ExchangeRates): Amount = target match {
      case `currency` => this.amount
      case _ => this.amount * exchangeRates(currency, target)
    }

    def ~>(target: Currency)(implicit exchangeRates: ExchangeRates): Transaction = target match {
      case `currency` => this
      case _ => copy(amount = this.amount * exchangeRates(currency, target), currency = target)
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

    def sumByPartnerAndCurrency(partner: Partner, currency: Currency): Option[Amount] =
      loadTransactions
        .filter(_.partner == partner)
        .map(_ |~> currency)
        .foldLeft(none[Amount])(_ |+| _.some)

    def sumByCurrency(currency: Currency): Option[PartnerAmountSummary] = {
      loadTransactions
        .map(_ ~> currency)
        .foldLeft(none[PartnerAmountSummary])((acc, tr) => acc |+| Map(tr.partner -> tr.amount).some) >>= write
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
