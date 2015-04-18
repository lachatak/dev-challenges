package org.kaloz.bigdataaggregator

import java.io.{File, FileOutputStream, PrintWriter}

import scala.math.BigDecimal.RoundingMode
import scala.util.Random

object TestFixture extends App {

  val currencies = List("HUF", "GBP", "EUR", "CHF", "USD", "JPY", "AUD", "BDT")

  initDefault()

  def initDefault() {
    val startTime = System.currentTimeMillis()

    delete("target/transactions.csv")
    delete("target/exchangerates.csv")

    createExchangeRatesFile()
    createTransactionsFile()

    println(s"Generation Time: ${(System.currentTimeMillis() - startTime) / 1000.00}s")
  }

  def createExchangeRatesFile(fileName: String = "target/exchangerates.csv") {


    val exchangeRates = for {
      sourceCurrency <- currencies
      targetCurrency <- currencies if targetCurrency != sourceCurrency
    } yield sourceCurrency + "," + targetCurrency + "," + randomNumber()

    writeFile(fileName, exchangeRates.iterator)
  }

  def createTransactionsFile(fileName: String = "target/transactions.csv", numOfPartners: Int = 10, numOfTransactions: Int = 1000 * 1000 * 10) {

    val partners = "KRS" :: List.fill(numOfPartners)(Random.alphanumeric.take(3).mkString)

    writeFile(fileName, Stream.continually(partners(Random.nextInt(partners.size)) + "," + currencies(Random.nextInt(currencies.length)) + "," + randomNumber())
      .take(numOfTransactions).iterator)
  }

  def writeFile(fileName: String, writable: Iterator[String]): Unit = {
    val printWriter = new PrintWriter(new FileOutputStream((fileName), true))
    try {
      writable.foreach(printWriter.println(_))
    } finally printWriter.close()
  }

  def randomNumber() = {
    val number = BigDecimal(Random.nextDouble() * 100)
    val scale = Math.min(3, number.scale)
    number.setScale(scale, RoundingMode.HALF_DOWN)
  }

  def delete(file: String) = new File(file).delete()

}
