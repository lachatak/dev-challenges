package org.kaloz.bigdataaggregator.infrastructure.driving

import org.kaloz.bigdataaggregator.Domain._
import org.kaloz.bigdataaggregator.infrastructure.driving.assembler.Transaction

import scala.io.Source

trait TransactionRepositoryComponentImpl extends TransactionRepositoryComponent {

  class FileTransactionRepositoryImpl(transactionFileName: String = "target/transactions.csv") extends TransactionRepository with FromFile {
    override def loadTransactions: TransactionFlow =
      fromFile(transactionFileName)
        .map(Transaction(_))
        .filter(_.isSuccess)
        .map(_.get)
  }

  class FileExchangeRateRepositoryImpl(exchangeFileName: String = "src/main/resources/exchangerates.csv") extends ExchangeRateRepository with FromFile {

    val EXCHANGE_PATTERN = "(.*),(.*),(.*)".r

    override def loadExchangeRates: ExchangeRates =
      fromFile(exchangeFileName)
        .collect { case EXCHANGE_PATTERN(from, to, amount) => (from.toUpperCase, to.toUpperCase) -> BigDecimal(amount)}
        .foldLeft(Map.empty[(Currency, Currency), Amount])(_ + _)
  }

  trait FromFile {
    def fromFile(transactionFileName: String): Iterator[String] = {
      Source
        .fromFile(transactionFileName)
        .getLines()
    }
  }

}
