package org.kaloz.bigdataaggregator

import org.apache.spark.{SparkConf, SparkContext}

object SparkMain {
  def process(targetCurrency: String) = {
    val sc = new SparkContext(new SparkConf().setSparkHome(System.getenv("SPARK_HOME")).setAppName("challenge").setMaster("local[4]").setJars(List(SparkContext.jarOfClass(this.getClass).get)))

    try {
      benchmark("Spark results: ", parse(sc, targetCurrency))
    } finally {
      sc.stop
    }
  }

  def parse(sc: SparkContext, targetCurrency: String) = {
    val rates = sc.textFile("/Users/krisztian.lachata/Documents/workspaces/pet/dev-challenges/bigdataaggregator/exchangerates.csv")
      .map(_.split(","))
      .filter { case Array(from, to, rate) => to == targetCurrency}
      .map { case Array(from, to, rate) => (from, rate.toDouble)}
      .collect()
      .toMap

    def exchange(currency: String, amount: String) = if (currency == targetCurrency) amount.toDouble else amount.toDouble * rates(currency)

    sc.textFile("/Users/krisztian.lachata/Documents/workspaces/pet/dev-challenges/bigdataaggregator/transactions.csv")
      .map(_.split(","))
      .map { case Array(partner, currency, amount) => (partner, exchange(currency, amount))}
      .reduceByKey(_ + _)
      .collect()
      .toMap
  }

  def main(args: Array[String]) {
    val targetCurrency = "GBP" // TODO parse args
    process(targetCurrency)
  }
}
