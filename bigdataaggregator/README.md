## Big Data Aggregator ##

This is my solution for **Dev Challenges** series **Big Data Aggregator** section.

## Technical details ##
- scala 2.11
- sbt 0.13.5

## How to run ##
```
sbt clean assembly
java -jar target/scala-2.11/bigdataaggregator-assembly-1.0.0.jar partner=KRS currency=GBP trFile=transactions.csv exFile=exchangerates.csv
```
Any of the parameters can abe omitted. They will be defaulted as it shown.
