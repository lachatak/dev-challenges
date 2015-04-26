package org.kaloz.bigdataaggregator

import org.apache.spark.{SparkConf, SparkContext}
import org.kaloz.bigdataaggregator.infrastructure.driving.assembler.{ExchangeRate, Transaction}

object SparkMain extends MainApp {

  val host = conf.getString("spark.main.host")

  benchmark("Spark group results: ", process {
    sc =>
      implicit val exchangeRates = sc.textFile(exchangerates)
        .map(ExchangeRate(_))
        .collect()
        .toMap

      sc.textFile(transactions)
        .map(Transaction(_))
        .map(tr => (tr.partner, (tr |~> currency)))
        .reduceByKey(_ + _)
        .collect()
        .toMap
  })

  benchmark("Spark amount results: ", process {
    sc =>
      implicit val exchangeRates = sc.textFile(exchangerates)
        .map(ExchangeRate(_))
        .collect()
        .toMap

      sc.textFile(transactions)
        .map(Transaction(_))
        .filter(_.partner == partner)
        .map(_ |~> currency)
        .fold(BigDecimal(0))(_ + _)
  })

  def process[T](handler: SparkContext => T): T = {
    val sc = new SparkContext(new SparkConf().setAppName("challenge").setMaster(host).setJars(List(SparkContext.jarOfClass(this.getClass).get)))
    try {
      handler(sc)
    } finally {
      sc.stop
    }
  }

}
