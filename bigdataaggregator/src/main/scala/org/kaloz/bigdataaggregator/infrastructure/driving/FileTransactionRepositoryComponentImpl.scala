package org.kaloz.bigdataaggregator.infrastructure.driving

import org.kaloz.bigdataaggregator.Domain._
import org.kaloz.bigdataaggregator.infrastructure.driving.assembler.{ExchangeRate, Transaction}

import scala.io.Source

trait FileTransactionRepositoryComponentImpl extends TransactionRepositoryComponent {

  class FileTransactionRepositoryImpl(transactionFileName: String) extends TransactionRepository with FromFile {
    override def loadTransactions: TransactionFlow =
      fromFile(transactionFileName)
        .map(Transaction(_))
  }

  class FileExchangeRateRepositoryImpl(exchangeFileName: String) extends ExchangeRateRepository with FromFile {

    override def loadExchangeRates: ExchangeRates =
      fromFile(exchangeFileName)
        .map(ExchangeRate(_))
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
