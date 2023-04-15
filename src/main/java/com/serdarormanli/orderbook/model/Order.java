package com.serdarormanli.orderbook.model;

public record Order(String id, Side side, int quantity, int price) {
    public Order decrementQuantity(int quantity) {
        return new Order(this.id, this.side, this.quantity - quantity, this.price);
    }
}
