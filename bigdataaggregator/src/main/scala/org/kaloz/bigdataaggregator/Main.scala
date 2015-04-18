package org.kaloz.bigdataaggregator

import org.kaloz.bigdataaggregator.Domain._
import org.kaloz.bigdataaggregator.infrastructure.driven.ResultWriterComponentImpl
import org.kaloz.bigdataaggregator.infrastructure.driving.TransactionRepositoryComponentImpl

object Main extends App with TransactionInfo with TransactionRepositoryComponentImpl with ResultWriterComponentImpl {

  val transactionRepository = new FileTransactionRepositoryImpl()

  val exchangeRateRepository = new FileExchangeRateRepositoryImpl()

  val resultWriter = new FileResultWriterImpl()

  var startTime = System.currentTimeMillis()
  val result = sumByCurrency("GBP")
  println(result)
  println(s"Time taken normal: ${(System.currentTimeMillis() - startTime) / 1000.00}s")

  startTime = System.currentTimeMillis()
  val sum = sumByPartnerAndCurrency("KRS", "GBP")
  println(sum)
  println(s"Time taken normal: ${(System.currentTimeMillis() - startTime) / 1000.00}s")

}
