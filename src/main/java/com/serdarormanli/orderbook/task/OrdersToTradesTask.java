package com.serdarormanli.orderbook.task;

import com.serdarormanli.orderbook.model.Order;
import com.serdarormanli.orderbook.output.Writer;
import com.serdarormanli.orderbook.service.OrderBook;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class OrdersToTradesTask implements Runnable {

    private final @NonNull OutputStreamWriter outputStreamWriter;
    private final @NonNull OrderBook orderBook;
    private final @NonNull Writer writer;
    private final @NonNull CountDownLatch countDownLatch;
    private final @NonNull BlockingQueue<Order> queue;

    @Override
    public void run() {
        try {
            // Poll normally until other task is running.
            while (this.countDownLatch.getCount() > 1) {
                var order = this.poll();
                order.ifPresent(this::addOrderAndWriteTrades);
            }

            // Drain rest of the orders to the list and add to the order book.
            var remainingOrders = new ArrayList<Order>();
            this.queue.drainTo(remainingOrders);

            for (var order : remainingOrders) {
                this.addOrderAndWriteTrades(order);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.countDownLatch.countDown();
        }
    }

    @SneakyThrows
    private Optional<Order> poll() {
        return Optional.ofNullable(this.queue.poll(10, TimeUnit.MILLISECONDS));
    }

    private void addOrderAndWriteTrades(@NonNull Order order) {
        var trades = this.orderBook.add(order);
        this.writer.write(this.outputStreamWriter, trades);
    }
}
