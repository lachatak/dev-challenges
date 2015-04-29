package org.kaloz.bigdataaggregator.config

import org.kaloz.bigdataaggregator.infrastructure.driven.ResultWriterComponentImpl
import org.kaloz.bigdataaggregator.infrastructure.driving.spark.SparkTransactionRepositoryComponentImpl

trait SparkConfig extends Config with SparkTransactionRepositoryComponentImpl with ResultWriterComponentImpl {

  val host = conf.getString("spark.main.host")

  val transactionRepository = new SparkTransactionRepositoryImpl(transactions, exchangerates)

  val resultWriter = new FileResultWriterImpl(aggregates)
}
