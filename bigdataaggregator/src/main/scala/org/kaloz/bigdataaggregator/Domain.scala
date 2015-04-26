package org.kaloz.bigdataaggregator

import scalaz.Scalaz._

object Domain {

  type Currency = String
  type Partner = String
  type Amount = BigDecimal
  type TransactionFlow = Iterator[Transaction]
  type PartnerAmountSummary = Map[Partner, Amount]
  type ExchangeRate = ((Currency, Currency), Amount)
  type ExchangeRates = Map[(Currency, Currency), Amount]

  case class Transaction(partner: Partner, currency: Currency, amount: Amount = BigDecimal(0)) {

    def |~>(target: Currency)(implicit exchangeRates: ExchangeRates): Option[Amount] = target match {
      case `currency` => this.amount.some
      case _ => exchangeRates.get(currency, target).map(_ * this.amount)
    }

    def ~>(target: Currency)(implicit exchangeRates: ExchangeRates): Option[Transaction] = target match {
      case `currency` => this.some
      case _ => exchangeRates.get(currency, target).map(c => copy(amount = this.amount * c, currency = target))
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
        .foldLeft(none[Amount])(_ |+| _)

    def sumByCurrency(currency: Currency): Option[PartnerAmountSummary] = {
      loadTransactions
        .map(_ ~> currency)
        .flatten
        .map(tr => Map(tr.partner -> tr.amount).some)
        .foldLeft(none[PartnerAmountSummary])((acc, tr) => acc |+| tr) >>= write
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
