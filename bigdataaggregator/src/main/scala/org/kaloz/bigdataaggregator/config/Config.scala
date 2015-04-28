package org.kaloz.bigdataaggregator.config

import com.typesafe.config.ConfigFactory

trait Config {

  val conf = ConfigFactory.load()

  val partner = conf.getString("partner")
  val currency = conf.getString("currency")
  val transactions = conf.getString("transactions")
  val exchangerates = conf.getString("exchange.rates")
  val aggregates = conf.getString("aggregates")

}
