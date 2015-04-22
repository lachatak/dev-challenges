package org.kaloz.bigdataaggregator

import org.kaloz.bigdataaggregator.Domain._
import org.kaloz.bigdataaggregator.infrastructure.driven.ResultWriterComponentImpl
import org.kaloz.bigdataaggregator.infrastructure.driving.FileTransactionRepositoryComponentImpl

import scala.collection.breakOut

object Main extends App with TransactionInfo with FileTransactionRepositoryComponentImpl with ResultWriterComponentImpl {

  val parsedArgs = parseArgs(args)

  val partner = parsedArgs.getOrElse("partner", "KRS")
  val currency = parsedArgs.getOrElse("currency", "GBP")
  val transactions = parsedArgs.getOrElse("trFile", "transactions.csv")
  val exchangerates = parsedArgs.getOrElse("exFile", "exchangerates.csv")

  println(s"Start processing with partner=$partner, currency=$currency, transactions=$transactions, exchangerates=$exchangerates ...")

  val transactionRepository = new FileTransactionRepositoryImpl(transactions)

  val exchangeRateRepository = new FileExchangeRateRepositoryImpl(exchangerates)

  val resultWriter = new FileResultWriterImpl()

  val result = benchmark("Process time from file -> result to file :", sumByCurrency(currency))
  println(result)

  val mem = benchmark("Process time from memory -> result to console :", result.get(partner))
  println(mem)

  val sum = benchmark("Process time form file -> result to console :",sumByPartnerAndCurrency(partner, currency))
  println(sum)

}
