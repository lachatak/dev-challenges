package org.kaloz.bigdataaggregator.config

import org.kaloz.bigdataaggregator.infrastructure.driven.ResultWriterComponentImpl
import org.kaloz.bigdataaggregator.infrastructure.driving.stream.StreamTransactionRepositoryComponentImpl

trait StreamConfig extends Config with StreamTransactionRepositoryComponentImpl with ResultWriterComponentImpl {

  val transactionRepository = new StreamTransactionRepositoryImpl(transactions, exchangerates)

  val resultWriter = new FileResultWriterImpl(aggregates)
}
