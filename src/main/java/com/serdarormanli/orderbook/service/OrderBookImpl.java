package com.serdarormanli.orderbook.service;

import com.serdarormanli.orderbook.model.Order;
import com.serdarormanli.orderbook.model.Side;
import com.serdarormanli.orderbook.model.Trade;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

public class OrderBookImpl implements OrderBook {

    private final EnumMap<Side, TreeMap<Integer, List<Order>>> ordersBySide = new EnumMap<>(Side.class);

    public OrderBookImpl() {
        // To sort buy orders descending by price.
        this.ordersBySide.put(Side.BUY, new TreeMap<>(Comparator.reverseOrder()));
        // To sort sell orders ascending by price.
        this.ordersBySide.put(Side.SELL, new TreeMap<>());
    }

    @Override
    public List<Trade> add(@NonNull Order order) {
        var trades = new ArrayList<Trade>();
        var price = order.price();
        var side = order.side();

        // Get opposite side orders.
        var opposingOrders = this.ordersBySide.get(side.opposite());
        // Get matching orders equal and less than the price.
        // For buy, it means prices numerical greater than and equal to price.
        // For sell, it means prices numerical less than and equal price.
        var matchedOpposingOrders = opposingOrders.headMap(price, true);

        // Iterate on the list of orders according to price ordering.
        var matchedOpposingOrdersIterator = matchedOpposingOrders.values().iterator();
        while (matchedOpposingOrdersIterator.hasNext()) {
            var matchedOpposingOrdersByPrice = matchedOpposingOrdersIterator.next();

            // Iterate on list of orders according to order creation time.
            var matchedOpposingOrdersByPriceIterator = matchedOpposingOrdersByPrice.listIterator();
            while (matchedOpposingOrdersByPriceIterator.hasNext()) {
                var opposingOrder = matchedOpposingOrdersByPriceIterator.next();
                // If the new order is reduced to zero, there is no need to consume the rest of orders.
                if (order.quantity() == 0) {
                    return trades;
                }

                var quantity = Math.min(opposingOrder.quantity(), order.quantity());

                opposingOrder = opposingOrder.decrementQuantity(quantity);
                order = order.decrementQuantity(quantity);

                // If the opposing order is reduced to zero, delete it.
                if (opposingOrder.quantity() == 0) {
                    matchedOpposingOrdersByPriceIterator.remove();
                } else {
                    matchedOpposingOrdersByPriceIterator.set(opposingOrder);
                }

                trades.add(new Trade(order.id(), opposingOrder.id(), opposingOrder.price(), quantity));
            }

            // If there are no orders left for the current price, remove it.
            if (matchedOpposingOrdersByPrice.isEmpty()) {
                matchedOpposingOrdersIterator.remove();
            }
        }

        // If the new order is reduced to zero, return trades.
        if (order.quantity() == 0) {
            return trades;
        }

        // Add the new order to the order book.
        var ordersAtPrice = this.ordersBySide.get(side).computeIfAbsent(price, i -> new ArrayList<>());
        ordersAtPrice.add(order);

        return trades;
    }

    @Override
    public NavigableMap<Integer, List<Order>> getOrders(@NonNull Side side) {
        var originalOrders = this.ordersBySide.get(side);

        var copy = new TreeMap<Integer, List<Order>>(originalOrders.comparator());
        originalOrders.forEach((price, orders) -> copy.put(price, List.copyOf(orders)));

        return copy;
    }
}
