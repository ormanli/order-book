# Order Book #

An exchange allows the buyers and sellers of a product to discover each other and trade. Buyers and sellers (traders)
submit orders to the exchange and the exchange applies simple rules to determine if a trade has occurred. The dominant
kind of exchange is a central limit order book (CLOB) where orders are matched using ‘price time priority’.

When placing an order, traders specify if they wish to buy or sell, the limit price ie. worst possible price they will
trade at, and the quantity (number of shares) they wish to trade. Trades only occur during the
processing of a newly posted order, and happen immediately, which is known as ‘continuous trading’.

### Requirements ###

* Java 19+
* Maven 3

### Info ###

* Application starts and creates `InputStreamReader` from `System.in` and `OutputStreamWriter` from `System.out`.
* There two threads, first one is reading orders from `InputStreamReader` and pushing to a `LinkedBlockingQueue`.
* Second thread is consuming orders from `LinkedBlockingQueue` and adding to `OrderBook`. If trades happen after adding
  an order, it prints trades to `OutputStreamWriter`.
* After both of the threads are finished, the order book is printed to `OutputStreamWriter`.
* If an exception is thrown, application tries to process the orders to order book as much as possible.

### How to create executable ###

```shell
mvn package
```

### How to run ###

```shell
java -jar ./target/orderbook-1.0-SNAPSHOT.jar < ./src/test/resources/test2.txt
```