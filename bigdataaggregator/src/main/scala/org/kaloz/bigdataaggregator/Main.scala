package org.kaloz.bigdataaggregator

import org.kaloz.bigdataaggregator.config.{Config, SparkConfig, StreamConfig}
import org.kaloz.bigdataaggregator.domain.Model.Transactions
import org.kaloz.bigdataaggregator.infrastructure.driven.benchmark

object Main extends Config with App {

  val appType = conf.getString("app.type")

  println(s"Start processing with app type $appType, partner=$partner, currency=$currency, transactions=$transactions, exchangerates=$exchangerates aggregates=$aggregates ...")

  val transactionsApp = appType match {
    case "spark" => new Transactions with SparkConfig
    case "stream" => new Transactions with StreamConfig
  }

  benchmark("Process time from file -> result to file :", transactionsApp.sumByCurrency(currency))

  benchmark("Process time form file -> result to console :", transactionsApp.sumByPartnerAndCurrency(partner, currency))

}
