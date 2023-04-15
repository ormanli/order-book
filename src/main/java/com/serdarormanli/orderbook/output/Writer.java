package com.serdarormanli.orderbook.output;

import com.serdarormanli.orderbook.model.Order;
import com.serdarormanli.orderbook.model.Trade;

import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;

public interface Writer {
    void write(OutputStreamWriter writer, List<Trade> trades);

    void write(OutputStreamWriter writer, Map<Integer, List<Order>> bids, Map<Integer, List<Order>> asks);
}
