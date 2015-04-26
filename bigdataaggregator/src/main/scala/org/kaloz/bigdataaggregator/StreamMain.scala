package org.kaloz.bigdataaggregator

import org.kaloz.bigdataaggregator.Domain._
import org.kaloz.bigdataaggregator.infrastructure.driven.ResultWriterComponentImpl
import org.kaloz.bigdataaggregator.infrastructure.driving.FileTransactionRepositoryComponentImpl

object StreamMain extends MainApp with TransactionInfo with FileTransactionRepositoryComponentImpl with ResultWriterComponentImpl {

  val transactionRepository = new FileTransactionRepositoryImpl(transactions)

  val exchangeRateRepository = new FileExchangeRateRepositoryImpl(exchangerates)

  val resultWriter = new FileResultWriterImpl(aggregates)

  benchmark("Process time from file -> result to file :", sumByCurrency(currency))

  benchmark("Process time form file -> result to console :", sumByPartnerAndCurrency(partner, currency))

}
