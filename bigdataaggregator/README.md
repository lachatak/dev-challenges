## Big Data Aggregator ##

This is my solution for **Dev Challenges** series [Big Data Aggregator](CHALLENGE.md) section.

## How to run ##
```
sbt clean assembly
java -jar target/scala-2.11/bigdataaggregator-assembly-1.0.0.jar partner=KRS currency=GBP trFile=transactions.csv exFile=exchangerates.csv
```
Any of the parameters can abe omitted. They will be defaulted as it shown.

### Results ###
- 10 randomly generated partner names in the transactions.csv file + 1 for validation which is my name "KRS"
- 10 cross exchange rates -> 90 items in the currencies.csv file
- 10.000.000 generated transactions for 10 partners and 10 currencies 
```
Start processing with partner=KRS, currency=GBP, transactions=transactions.csv, exchangerates=exchangerates.csv ...
Some(Map(Cj0AT -> 17687007.894836635508, Vnd9d -> 17642585.361019262488, NhI0s -> 17670824.006035535860, xrr0G -> 17702774.485676729032, 36UMO -> 17712700.568250546474, U4Bt5 -> 17660737.336327469313, KRS -> 17716558.433476505763, aGrgU -> 17707874.304117732802, hyzKM -> 17719373.257142058751, DIC4e -> 17667957.933976776286, aHW5r -> 17672595.781572634203))
Process time from file -> result to file: 9.24s
17716558.433476505763
Process time from memory -> result to console: 0.0s
Some(17716558.433476505763)
Process time form file -> result to console: 4.031s
```

### Technical details ###
- scala 2.11.4
- sbt 0.13.5
- scalaz 7.1.0
- [cake pattern](http://jonasboner.com/2008/10/06/real-world-scala-dependency-injection-di/) dependency injection for scala
- Domain Driven Design. Separated infrastructure/app/domain layers implemented in scala
- [Scala spec2](https://etorreborre.github.io/specs2/guide/SPECS2-3.5/org.specs2.guide.UserGuide.html) for testing
- Input validation. Invalid transaction or currency doesn't break the calculation
- Typesafe custom types
```scala
  type Currency = String
  type Partner = String
  type Amount = BigDecimal
  type TransactionFlow = Iterator[Transaction]
  type PartnerAmountSummary = Map[Partner, Amount]
  type ExchangeRates = Map[(Currency, Currency), Amount]
```
