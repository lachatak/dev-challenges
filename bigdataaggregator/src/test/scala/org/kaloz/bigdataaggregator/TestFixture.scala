package org.kaloz.bigdataaggregator

import java.io.{File, FileOutputStream, PrintWriter}

import scala.math.BigDecimal.RoundingMode
import scala.util.Random

object TestFixture extends App {

  val numberOfPartners = 10

  val currencies = List("HUF", "GBP", "EUR", "CHF", "USD", "JPY", "AUD", "BDT")

  val partners = "KRS" :: List.fill(numberOfPartners)(Random.alphanumeric.take(3).mkString)

  initDefault()

  def initDefault() {
    val startTime = System.currentTimeMillis()
    delete("target/transactions.csv")
    delete("target/exchangerates.csv")
    createExchangeRatesFile("target/exchangerates.csv")
    createTransactionsFile("target/transactions.csv", 1000 * 1000 * 10)
    println(s"Generation Time: ${(System.currentTimeMillis() - startTime) / 1000.00}s")
  }

  def createExchangeRatesFile(file: String) {

    val exchangeRates = for {
      sourceCurrency <- currencies
      targetCurrency <- currencies if targetCurrency != sourceCurrency
    } yield sourceCurrency + "," + targetCurrency + "," + randomNumber()

    val printWriter = new PrintWriter(new FileOutputStream((file), true))
    try {
      exchangeRates.foreach(printWriter.println(_))
    } finally printWriter.close()
  }

  def createTransactionsFile(file: String, numOfTransactions: Int) {
    val printWriter = new PrintWriter(new FileOutputStream((file), true))
    try {
      Stream
        .continually(partners(Random.nextInt(partners.size)) + "," + currencies(Random.nextInt(currencies.length)) + "," + randomNumber())
        .take(numOfTransactions)
        .foreach(printWriter.println(_))
    } finally printWriter.close()

  }

  def randomNumber() = {
    val number = BigDecimal(Random.nextDouble() * 100)
    val scale = Math.min(3, number.scale)
    number.setScale(scale, RoundingMode.HALF_DOWN)
  }

  def delete(file: String) = new File(file).delete()

}
