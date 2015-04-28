package org.kaloz.bigdataaggregator.infrastructure.driving

import org.kaloz.bigdataaggregator.Domain._
import org.kaloz.bigdataaggregator.infrastructure.driving.assembler.ExchangeRate

trait FileExchangeRateRepositoryComponentImpl extends ExchangeRateRepositoryComponent {

  class FileExchangeRateRepositoryImpl(exchangeFileName: String) extends ExchangeRateRepository with FromFile {

    override def loadExchangeRates: ExchangeRates =
      fromFile(exchangeFileName)
        .map(ExchangeRate(_))
        .foldLeft(Map.empty[(Currency, Currency), Amount])(_ + _)
  }

}
