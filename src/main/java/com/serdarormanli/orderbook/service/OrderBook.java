package com.serdarormanli.orderbook.service;

import com.serdarormanli.orderbook.model.Order;
import com.serdarormanli.orderbook.model.Side;
import com.serdarormanli.orderbook.model.Trade;

import java.util.List;
import java.util.NavigableMap;

public interface OrderBook {
    List<Trade> add(Order order);

    NavigableMap<Integer, List<Order>> getOrders(Side side);
}
