## Challenge 1 - Big data aggregator ##
Let's assume we have 2 CSV files. The first file has a list of exchange rates and the format of the content is lines of (CURRENCY1,CURRENCY2,AMOUNT):  
```
HUF,GBP,0.0025
USD,GBP,0.67
CHF,GBP,0.71
GBP,HUF,421.5
```
You can assume the that the file is correct, that is, it doesn't contain any duplicates, contradictions, etc. It is also symmetrical, that is, if the exchange rate is present for any X->Y currencies, it is also present for Y->X. The values are arbitrary, not necessarily reciprocal to each other and not necessary "real". The size of the file is ~10k lines (100 different currencies).
An example exchangerates.csv can be found here.
The other file could contain possibly billions of transactions (too many to hold everything in memory). One transaction has a partner name, currency and amount. The format of the content is lines of (PARTNER,CURRENCY,AMOUNT):
```
Unlimited ltd.,GBP,200.5
Local plumber ltd.,GBP,50.2
Defence ltd.,USD,350.3
Local plumber ltd.,HUF,35000
Unlimited ltd.,CHF,157.0
```
You can assume that all the currencies mentioned in this file are present in the exchangerates.csv. The number of partners won't exceed 100.

### Task 1 ###
Provide the optimal (with regards to memory footprint) solution to calculate the aggregated summary for transactions group by partner and currency denominated in a given currency. Write the result in a new file (PARTNER,AMOUNT). This example is denominated in GBP:
```
Unlimited ltd.,311.25
Local plumber ltd.,136.56
Defence ltd,234.75
```

### Task 2 ###
Provide the optimal (with regards to memory footprint) solution to calculate the aggregated amount of transactions for a specific partner denominated in a given currency. Write the result to the console in the form like this (AMOUNT):
```
311.25
```
This is the result of aggregating transactions for partner  Unlimited ltd.  and currency  GBP.
### Interface ###

#### Input ###
File path of transactions.csv (e.g. "/Users/joe/transactions.csv")
File path of exchangerates.csv (e.g. "/Users/joe/exchangerates.csv")
Partner (e.g. "Defence ltd.")
Target currency (e.g. "GBP")
Starting example
```
java -jar Aggregator.jar transactions.csv exchangerates.csv "Defence ltd." GBP
```
#### Output ####
- For Task 1: aggregated_transactions_by_partner.csv (in any folder)
- For Task 2: console log
- You can prepare your application as a jar file to provide a command line interface, but you might as well just have a main method where you manually type the input if you don't want to bother with packaging the app. This is for making life easier for developers who want to implement their solution in a non-JVM language (e.g. javascript).

#### Other requirements ####
- Generating the input files is straightforward, so everyone can implement it for herself, we won't provide them here.
- Test coverage is not a requirement, but have at least one end-to-end test.
- If you only implement one task, it's fine. The input is the same for both.
- Feel free to pick any technology which fits to the problem
