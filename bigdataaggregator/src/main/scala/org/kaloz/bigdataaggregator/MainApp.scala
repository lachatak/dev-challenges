package org.kaloz.bigdataaggregator

import org.kaloz.bigdataaggregator.StreamMain._

import scala.collection.mutable.ListBuffer


trait MainApp extends DelayedInit {

  val partner = parsedArgs.getOrElse("partner", "KRS")
  val currency = parsedArgs.getOrElse("currency", "GBP")
  val transactions = parsedArgs.getOrElse("trFile", "transactions.csv")
  val exchangerates = parsedArgs.getOrElse("exFile", "exchangerates.csv")

  println(s"Start processing with partner=$partner, currency=$currency, transactions=$transactions, exchangerates=$exchangerates ...")

  val result = benchmark("Process time from file -> result to file :", sumByCurrency(currency))

  benchmark("Process time from memory -> result to console :", result.get(partner))

  benchmark("Process time form file -> result to console :", sumByPartnerAndCurrency(partner, currency))

  private val initCode = new ListBuffer[() => Unit]

  override def delayedInit(body: => Unit) {
    initCode += (() => body)
  }

  def main(args: Array[String]) {

    val parsedArgs = parseArgs(args)

    for (proc <- initCode) proc()

    val result = benchmark("Process time from file -> result to file :", sumByCurrency(currency))

    benchmark("Process time from memory -> result to console :", result.get(partner))

    benchmark("Process time form file -> result to console :", sumByPartnerAndCurrency(partner, currency))
  }
}
