package com.serdarormanli.orderbook.model;

public enum Side {
    BUY, SELL;

    public Side opposite() {
        return switch (this) {
            case BUY -> SELL;
            case SELL -> BUY;
        };
    }
}
