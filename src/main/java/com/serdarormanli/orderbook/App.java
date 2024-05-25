package com.serdarormanli.orderbook;

import com.serdarormanli.orderbook.input.CSVParser;
import com.serdarormanli.orderbook.model.Order;
import com.serdarormanli.orderbook.model.Side;
import com.serdarormanli.orderbook.output.CustomTextWriter;
import com.serdarormanli.orderbook.service.OrderBookImpl;
import com.serdarormanli.orderbook.task.InputToOrdersTask;
import com.serdarormanli.orderbook.task.OrdersToTradesTask;
import lombok.Cleanup;
import lombok.SneakyThrows;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class App {

    @SneakyThrows
    public static void main(String[] args) {
        @Cleanup("shutdown") var executorService = Executors.newVirtualThreadPerTaskExecutor();

        @Cleanup var output = new OutputStreamWriter(System.out, Charset.defaultCharset());
        @Cleanup var input = new InputStreamReader(System.in, Charset.defaultCharset());

        var countDownLatch = new CountDownLatch(2);

        var orderQueue = new LinkedBlockingQueue<Order>();
        var orderBook = new OrderBookImpl();

        var writer = new CustomTextWriter();
        var parser = new CSVParser();

        var inputTask = new InputToOrdersTask(input, parser, countDownLatch, orderQueue);
        var ordersToTradesTask = new OrdersToTradesTask(output, orderBook, writer, countDownLatch, orderQueue);

        executorService.submit(ordersToTradesTask);
        executorService.submit(inputTask);

        countDownLatch.await();
        writer.write(output, orderBook.getOrders(Side.BUY), orderBook.getOrders(Side.SELL));
    }
}
