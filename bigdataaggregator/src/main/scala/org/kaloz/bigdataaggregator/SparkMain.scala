package org.kaloz.bigdataaggregator

import org.apache.spark.{SparkConf, SparkContext}
import org.kaloz.bigdataaggregator.Domain.{Currency, Partner}
import org.kaloz.bigdataaggregator.infrastructure.driving.assembler.{ExchangeRate, Transaction}

import scala.util.Success

object SparkMain extends MainApp {

  process(conf.getString("spark.main.host"), partner, currency)

  def process(host: String, partner: Partner, currency: Currency) = {
    val sc = new SparkContext(new SparkConf().setAppName("challenge").setMaster(host).setJars(List(SparkContext.jarOfClass(this.getClass).get)))

    try {
      benchmark("Spark results: ", parse(sc, partner, currency))
    } finally {
      sc.stop
    }
  }

  def parse(sc: SparkContext, partner: Partner, target: Currency) = {

    implicit val exchangeRates = sc.textFile(exchangerates)
      .map(ExchangeRate(_))
      .collect { case Success(s) => s}
      .collect()
      .toMap

    sc.textFile(transactions)
      .map(Transaction(_))
      .collect { case Success(s) => s ~> currency}
      .collect { case Some(s) => (s.partner, s.amount)}
      .reduceByKey(_ + _)
      .collect()
      .toMap
  }

}
