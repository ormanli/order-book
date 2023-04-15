package com.serdarormanli.orderbook.input;

import com.serdarormanli.orderbook.model.Order;

import java.io.Reader;
import java.util.Queue;

public interface Parser {
    void parse(Reader reader, Queue<Order> queue);
}
