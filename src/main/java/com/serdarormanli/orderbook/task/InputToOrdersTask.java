package com.serdarormanli.orderbook.task;

import com.serdarormanli.orderbook.input.Parser;
import com.serdarormanli.orderbook.model.Order;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import java.io.InputStreamReader;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;

@Log
@RequiredArgsConstructor
public class InputToOrdersTask implements Runnable {

    private final @NonNull InputStreamReader inputStreamReader;
    private final @NonNull Parser parser;
    private final @NonNull CountDownLatch countDownLatch;
    private final @NonNull Queue<Order> queue;

    @Override
    public void run() {
        try {
            this.parser.parse(this.inputStreamReader, this.queue);
        } catch (Exception e) {
            log.severe(e.getMessage());
        } finally {
            this.countDownLatch.countDown();
        }
    }
}
