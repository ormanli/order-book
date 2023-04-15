package com.serdarormanli.orderbook.model;

public record Trade(String aggressingOrderID, String restingOrderID, int matchPrice, int quantity) {
}
