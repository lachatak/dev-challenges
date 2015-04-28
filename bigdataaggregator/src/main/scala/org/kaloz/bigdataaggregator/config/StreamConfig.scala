package org.kaloz.bigdataaggregator.config

import org.kaloz.bigdataaggregator.infrastructure.driven.ResultWriterComponentImpl
import org.kaloz.bigdataaggregator.infrastructure.driving.{FileExchangeRateRepositoryComponentImpl, FileTransactionRepositoryComponentImpl}

trait StreamConfig extends Config with FileTransactionRepositoryComponentImpl with FileExchangeRateRepositoryComponentImpl with ResultWriterComponentImpl {

  val transactionRepository = new FileTransactionRepositoryImpl(transactions)

  val exchangeRateRepository = new FileExchangeRateRepositoryImpl(exchangerates)

  val resultWriter = new FileResultWriterImpl(aggregates)
}
