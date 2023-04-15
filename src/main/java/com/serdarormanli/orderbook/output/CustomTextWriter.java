package com.serdarormanli.orderbook.output;

import com.serdarormanli.orderbook.model.Order;
import com.serdarormanli.orderbook.model.Side;
import com.serdarormanli.orderbook.model.Trade;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CustomTextWriter implements Writer {

    private static final Order EMPTY_ORDER = new Order("", Side.BUY, 0, 0);

    private String formatTrade(@NonNull Trade trade) {
        return "trade %s,%s,%d,%d\n".formatted(trade.aggressingOrderID(), trade.restingOrderID(), trade.matchPrice(), trade.quantity());
    }

    private String formatOrderBookLine(int buyQuantity, int buyPrice, int sellPrice, int sellQuantity) {
        var buyQuantityStr = this.formatQuantity(buyQuantity);
        var buyPriceStr = this.formatPrice(buyPrice);

        var sellPriceStr = this.formatPrice(sellPrice);
        var sellQuantityStr = this.formatQuantity(sellQuantity);

        return "%s %s | %s %s\n".formatted(buyQuantityStr, buyPriceStr, sellPriceStr, sellQuantityStr);
    }

    private String alignRight(@NonNull String s, int width) {
        return " ".repeat(width - s.length()) + s;
    }

    private String formatPrice(int price) {
        var s = price == 0 ? "" : Integer.toString(price);
        return this.alignRight(s, 6);
    }

    private String formatQuantity(int quantity) {
        var s = quantity == 0 ? "" : String.format(Locale.US, "%,d", quantity);
        return this.alignRight(s, 11);
    }

    @Override
    @SneakyThrows(IOException.class)
    public void write(@NonNull OutputStreamWriter writer,
                      @NonNull List<Trade> trades) {
        for (var trade : trades) {
            writer.write(this.formatTrade(trade));
        }

        writer.flush();
    }

    @Override
    @SneakyThrows(IOException.class)
    public void write(@NonNull OutputStreamWriter writer,
                      @NonNull Map<Integer, List<Order>> bids,
                      @NonNull Map<Integer, List<Order>> asks) {
        var flattenBids = bids.values().stream().flatMap(List::stream).toList();
        var flattenAsks = asks.values().stream().flatMap(List::stream).toList();

        var maxLength = Math.max(flattenBids.size(), flattenAsks.size());

        for (var i = 0; i < maxLength; i++) {
            var bid = i < flattenBids.size() ? flattenBids.get(i) : EMPTY_ORDER;
            var ask = i < flattenAsks.size() ? flattenAsks.get(i) : EMPTY_ORDER;

            writer.write(this.formatOrderBookLine(bid.quantity(), bid.price(), ask.price(), ask.quantity()));
        }

        writer.flush();
    }
}
