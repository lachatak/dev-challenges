package org.kaloz.bigdataaggregator

import org.kaloz.bigdataaggregator.Domain._
import org.kaloz.bigdataaggregator.infrastructure.driven.ResultWriterComponentImpl
import org.kaloz.bigdataaggregator.infrastructure.driving.TransactionRepositoryComponentImpl

import scala.collection.breakOut

object Main extends App with TransactionInfo with TransactionRepositoryComponentImpl with ResultWriterComponentImpl {

  val Opt = """(\S+)=(\S+)""".r

  val parsedArgs: Map[String, String] = args.collect { case Opt(key, value) => key -> value}(breakOut)

  val partner = parsedArgs.getOrElse("partner", "KRS")
  val currency = parsedArgs.getOrElse("currency", "GBP")
  val transactions = parsedArgs.getOrElse("trFile", "transactions.csv")
  val exchangerates = parsedArgs.getOrElse("exFile", "exchangerates.csv")

  println(s"Start processing with partner=$partner, currency=$currency, transactions=$transactions, exchangerates=$exchangerates ...")

  val transactionRepository = new FileTransactionRepositoryImpl(transactions)

  val exchangeRateRepository = new FileExchangeRateRepositoryImpl(exchangerates)

  val resultWriter = new FileResultWriterImpl()

  var startTime = System.currentTimeMillis()
  val result = sumByCurrency(currency)
  println(result)
  println(s"Time taken normal: ${(System.currentTimeMillis() - startTime) / 1000.00}s")

  startTime = System.currentTimeMillis()
  val sum = sumByPartnerAndCurrency(partner, currency)
  println(sum)
  println(s"Time taken normal: ${(System.currentTimeMillis() - startTime) / 1000.00}s")

}
