package org.kaloz.bigdataaggregator.infrastructure.driving.spark

import org.apache.spark.{SparkConf, SparkContext}
import org.kaloz.bigdataaggregator.domain.Model._
import org.kaloz.bigdataaggregator.domain.TransactionRepositoryComponent
import org.kaloz.bigdataaggregator.infrastructure.driving.assembler.{ExchangeRate, Transaction}

trait SparkTransactionRepositoryComponentImpl extends TransactionRepositoryComponent {

  def host: String

  class SparkTransactionRepositoryImpl(transactionFileName: String, exchangeFileName: String) extends TransactionRepository {

    val rates =
      fromSpark() {
        (sc, currency, partner, rates) =>
          sc.textFile(exchangeFileName)
            .flatMap(ExchangeRate(_))
            .collect
            .toMap
      }

    override def loadTransactionsSumByCurrency(currency: Currency): Option[PartnerAmountSummary] = {
      fromSpark(currency) {
        (sc, currency, partner, rates) =>
          implicit val r = rates
          sc.textFile(transactionFileName)
            .flatMap(Transaction(_))
            .map(tr => (tr.partner, tr |~> currency))
            .reduceByKey(_ + _)
            .collect
            .toMap match {
            case m: Map[Partner, Amount] if m.isEmpty => None
            case m => Some(m)
          }
      }
    }

    override def loadTransactionsSumByPartnerAndCurrency(partner: Partner, currency: Currency): Amount = {
      fromSpark(currency, partner) {
        (sc, currency, partner, rates) =>
          implicit val r = rates
          sc.textFile(transactionFileName)
            .flatMap(Transaction(_))
            .filter(_.partner == partner)
            .map(_ |~> currency)
            .fold(BigDecimal(0))(_ + _)
      }
    }

    def fromSpark[T](currency: Currency = "", partner: Partner = "")(handler: (SparkContext, Currency, Partner, ExchangeRates) => T): T = {
      val sparkConf: SparkConf = new SparkConf()
        .setAppName("bigdataaggregator")
        .setMaster(host)
        .setJars(List(SparkContext.jarOfClass(this.getClass).get))
        .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
        .set("spark.kryoserializer.buffer.mb", "24")

      val sc = new SparkContext(sparkConf)

      try {
        handler(sc, currency, partner, rates)
      } finally {
        sc.stop
      }
    }
  }

}

