package org.kaloz.bigdataaggregator

import com.typesafe.config.ConfigFactory


trait MainApp extends App {

  val conf = ConfigFactory.load()

  val partner = conf.getString("partner")
  val currency = conf.getString("currency")
  val transactions = conf.getString("transactions")
  val exchangerates = conf.getString("exchange.rates")
  val aggregates = conf.getString("aggregates")

  println(s"Start processing with partner=$partner, currency=$currency, transactions=$transactions, exchangerates=$exchangerates aggregates=$aggregates ...")
}
