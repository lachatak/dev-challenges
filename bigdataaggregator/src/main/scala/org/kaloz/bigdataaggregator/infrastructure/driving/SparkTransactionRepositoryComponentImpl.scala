package org.kaloz.bigdataaggregator.infrastructure.driving

import org.apache.spark.{SparkConf, SparkContext}
import org.kaloz.bigdataaggregator.Domain._
import org.kaloz.bigdataaggregator.infrastructure.driving.assembler.{ExchangeRate, Transaction}

trait SparkTransactionRepositoryComponentImpl extends TransactionRepositoryComponent {

  def host: String

  class SparkTransactionRepositoryImpl(transactionFileName: String, exchangeFileName: String) extends TransactionRepository {

    override def loadTransactionsSumByCurrency(currency: Currency): Option[PartnerAmountSummary] = {
      process(currency, "") {
        (sc, currency, partner) =>
          implicit val rates = sc.textFile(exchangeFileName)
            .map(ExchangeRate(_))
            .collect
            .toMap

          sc.textFile(transactionFileName)
            .map(Transaction(_))
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
      process(currency, partner) {
        (sc, currency, partner) =>
          implicit val rates = sc.textFile(exchangeFileName)
            .map(ExchangeRate(_))
            .collect()
            .toMap

          sc.textFile(transactionFileName)
            .map(Transaction(_))
            .filter(_.partner == partner)
            .map(_ |~> currency)
            .fold(BigDecimal(0))(_ + _)
      }
    }

    def process[T](currency: Currency, partner: Partner)(handler: (SparkContext, Currency, Partner) => T): T = {
      val sparkConf: SparkConf = new SparkConf()
        .setAppName("bigdataaggregator")
        .setMaster(host)
        .setJars(List(SparkContext.jarOfClass(this.getClass).get))
        .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
        .set("spark.kryoserializer.buffer.mb", "24")

      val sc = new SparkContext(sparkConf)

      try {
        handler(sc, currency, partner)
      } finally {
        sc.stop
      }
    }
  }
}

