## Big Data Aggregator ##

This is my solution for **Dev Challenges** series [Big Data Aggregator](CHALLENGE.md) section.
There are 2 different implementations under the hood to be able to compare solutions.

## How to run ##
```
sbt clean assembly
java -jar -Dapp.type=stream -Dpartner=KRS -Dcurrency=GBP -Dtransactions=transactions.csv -Dexchange.rates=exchangerates.csv -Daggregates=aggregate.csv target/scala-2.11/bigdataaggregator.jar
```
Any of the parameters can abe omitted. They will be defaulted from the [application.conf](src/main/resources/application.conf).

#### Application types ####
- stream. Uses simple scala file streams
- spark. Uses spark to process the provided transactions.csv file. This is the default application type

### Results with streams ###
- 10 randomly generated partner names in the transactions.csv file + 1 for validation which is my name "KRS"
- 10 cross exchange rates -> 90 items in the currencies.csv file
- 100.000.000 generated transactions for 10 partners and 10 currencies
```
krisztian.lachata@GL05152M:bigdataaggregator [master]$ java -jar -Dapp.type=stream target/scala-2.11/bigdataaggregator.jar
Start processing with app type stream, partner=KRS, currency=GBP, transactions=transactions.csv, exchangerates=exchangerates.csv aggregates=aggregate.csv ...
Some(Map(4UC4S -> 176822331.488623929706, GZHVT -> 176925240.925915505963, BH2CY -> 176843215.517065683155, FJVL6 -> 177001103.827384237019, KRS -> 176717265.296737444261, 8DYKE -> 176828607.667209633167, XEWSK -> 176839422.731465110378, WUOYP -> 176817384.914996858286, 7CIUR -> 176816128.647459041147, LNP4O -> 177051696.268582293814, GKP6U -> 176799882.728319962430))
Process time from file -> result to file : 88.078s
176717265.296737444261
Process time form file -> result to console : 44.855s
```

### Results with Spark ###
- 10 randomly generated partner names in the transactions.csv file + 1 for validation which is my name "KRS"
- 10 cross exchange rates -> 90 items in the currencies.csv file
- 100.000.000 generated transactions for 10 partners and 10 currencies
```
krisztian.lachata@GL05152M:bigdataaggregator [master]$ java -jar -Dapp.type=spark target/scala-2.11/bigdataaggregator.jar
Start processing with app type stream, partner=KRS, currency=GBP, transactions=transactions.csv, exchangerates=exchangerates.csv aggregates=aggregate.csv ...
Some(Map(4UC4S -> 176822331.488623929706, GZHVT -> 176925240.925915505963, BH2CY -> 176843215.517065683155, FJVL6 -> 177001103.827384237019, KRS -> 176717265.296737444261, 8DYKE -> 176828607.667209633167, XEWSK -> 176839422.731465110378, WUOYP -> 176817384.914996858286, 7CIUR -> 176816128.647459041147, LNP4O -> 177051696.268582293814, GKP6U -> 176799882.728319962430))
Process time from file -> result to file : 27.602s
176717265.296737444261
Process time form file -> result to console : 20.322s
```
If you have a running spark cluster you could pass the URl of the master node as **-Dspark.main.host=spark://HOST:POST**. With this you could extend the processing capacity. For the tests sake it is running in embedded mode.
### Technical details ###
- scala 2.11.4
- sbt 0.13.5
- scalaz 7.1.0
- [cake pattern](http://jonasboner.com/2008/10/06/real-world-scala-dependency-injection-di/) dependency injection for scala
- [Spark](http://spark.apache.org/docs/latest/index.html) for cluster computing transaction file
- Domain Driven Design. Separated infrastructure/app/domain layers implemented in scala
- [Scala spec2](https://etorreborre.github.io/specs2/guide/SPECS2-3.5/org.specs2.guide.UserGuide.html) for testing
- Input validation. Invalid transaction or currency doesn't break the calculation
- Typesafe custom types
```scala
  type Currency = String
  type Partner = String
  type Amount = BigDecimal
  type Rate = BigDecimal
  type PartnerAmountSummary = Map[Partner, Amount]
  type ExchangeRate = ((Currency, Currency), Rate)
  type ExchangeRates = Map[(Currency, Currency), Rate]
```
