package org.kaloz.bigdataaggregator

import org.kaloz.bigdataaggregator.Domain._
import org.kaloz.bigdataaggregator.infrastructure.driven.ResultWriterComponentImpl
import org.kaloz.bigdataaggregator.infrastructure.driving.TransactionRepositoryComponentImpl

import scala.collection.breakOut

object Main extends App with TransactionInfo with TransactionRepositoryComponentImpl with ResultWriterComponentImpl {

  val transactionRepository = new FileTransactionRepositoryImpl()

  val exchangeRateRepository = new FileExchangeRateRepositoryImpl()

  val resultWriter = new FileResultWriterImpl()

  val Opt = """(\S+)=(\S+)""".r

  val parsedArgs: Map[String, String] = args.collect { case Opt(key, value) => key -> value}(breakOut)
  val partner = parsedArgs.getOrElse("partner", "KRS")
  val currency = parsedArgs.getOrElse("currency", "GBP")

  var startTime = System.currentTimeMillis()
  val result = sumByCurrency(currency)
  println(result)
  println(s"Time taken normal: ${(System.currentTimeMillis() - startTime) / 1000.00}s")

  startTime = System.currentTimeMillis()
  val sum = sumByPartnerAndCurrency(partner, currency)
  println(sum)
  println(s"Time taken normal: ${(System.currentTimeMillis() - startTime) / 1000.00}s")

}
