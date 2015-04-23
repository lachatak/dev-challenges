package org.kaloz.bigdataaggregator

import org.apache.spark.{SparkConf, SparkContext}
import SparkContext.rddToPairRDDFunctions

object HelloSpark {
  def process(targetCurrency: String) = {
    val sc = new SparkContext(new SparkConf().setAppName("challenge1").setMaster("local[2]"))

    try {
      val results = benchmark("log", parse(sc, targetCurrency))
      println(results.toList)

    } finally {
      sc.stop
    }
  }

  def parse(sc: SparkContext, targetCurrency: String) = {
    val rates = sc.textFile("exchangerates.csv")
      .map(_.split(","))
      .filter { case Array(from, to, rate) => to == targetCurrency }
      .map { case Array(from, to, rate) => (from, rate.toDouble) }
      .collect()
      .toMap

    def exchange(currency: String, amount: String) = if (currency == targetCurrency) amount.toDouble else amount.toDouble * rates(currency)

    sc.textFile("transactions.csv")
      .map(_.split(","))
      .map { case Array(partner, currency, amount) => (partner, exchange(currency, amount)) }
      .reduceByKey(_ + _)
      .collect()
  }

  def main(args: Array[String]) {
    val targetCurrency = "GBP" // TODO parse args
    process(targetCurrency)
  }
}
