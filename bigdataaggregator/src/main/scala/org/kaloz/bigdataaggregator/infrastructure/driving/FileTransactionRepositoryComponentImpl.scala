package org.kaloz.bigdataaggregator.infrastructure.driving

import org.kaloz.bigdataaggregator.Domain._
import org.kaloz.bigdataaggregator.infrastructure.driving.assembler.Transaction

import scala.io.Source

trait TransactionRepositoryComponentImpl extends TransactionRepositoryComponent {

  class FileTransactionRepositoryImpl(transactionFileName: String = "target/transactions.csv") extends TransactionRepository {
    override def loadTransactions: TransactionFlow = Source.fromFile(transactionFileName).getLines().map(Transaction(_))
  }

  class FileExchangeRateRepositoryImpl(exchangeFileName: String = "target/exchangerates.csv") extends ExchangeRateRepository {

    val EXCHANGE_PATTERN = "(.*),(.*),(.*)".r

    override def loadExchangeRates: ExchangeRates = Source.fromFile(exchangeFileName).getLines()
      .collect {
      case EXCHANGE_PATTERN(from, to, amount) => (from, to) -> BigDecimal(amount)
    }.foldLeft(Map.empty[(Currency, Currency), Amount])(_ + _)
  }

}
