package org.kaloz.bigdataaggregator

import java.io.{File, FileOutputStream, PrintWriter}

import scala.math.BigDecimal.RoundingMode
import scala.util.Random

object TestFixture extends App {

  initDefault()

  def initDefault() {
    val startTime = System.currentTimeMillis()

    delete()

    createTransactionsFile()

    println(s"Generation Time: ${(System.currentTimeMillis() - startTime) / 1000.00}s")
  }

  def createTransactionsFile(fileName: String = "transactions.csv", numOfPartners: Int = 10, numOfTransactions: Int = 1000 * 1000 * 10) {

    val currencies = List("USD", "EUR", "GBP", "AUD", "JPY", "RUB", "CHF", "HUF", "PLN", "SEK")
    val partners = "KRS" :: List.fill(numOfPartners)(Random.alphanumeric.take(5).mkString)

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
    val scale = Math.min(2, number.scale)
    number.setScale(scale, RoundingMode.HALF_DOWN)
  }

  def delete(file: String = "transactions.csv") = new File(file).delete()

}
