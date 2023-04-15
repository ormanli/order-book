package com.serdarormanli.orderbook.service;

import com.serdarormanli.orderbook.model.Order;
import com.serdarormanli.orderbook.model.Side;
import com.serdarormanli.orderbook.model.Trade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderBookTest {
    private OrderBookImpl orderBook;

    @BeforeEach
    void setUp() {
        this.orderBook = new OrderBookImpl();
    }

    @Test
    void example1() {
        assertThat(this.orderBook.add(new Order("a", Side.BUY, 1_000, 99))).isEmpty();
        assertThat(this.orderBook.add(new Order("b", Side.BUY, 1_200, 98))).isEmpty();
        assertThat(this.orderBook.add(new Order("c", Side.BUY, 500, 99))).isEmpty();

        assertThat(this.orderBook.add(new Order("d", Side.SELL, 2_000, 101))).isEmpty();

        var buyOrders = this.orderBook.getOrders(Side.BUY);
        assertThat(buyOrders).hasSize(2).containsKeys(99, 98);
        assertThat(buyOrders).extractingByKey(99).asList()
                .containsExactly(
                        new Order("a", Side.BUY, 1_000, 99),
                        new Order("c", Side.BUY, 500, 99)
                );
        assertThat(buyOrders).extractingByKey(98).asList()
                .containsExactly(
                        new Order("b", Side.BUY, 1_200, 98)
                );

        var sellOrders = this.orderBook.getOrders(Side.SELL);
        assertThat(sellOrders).hasSize(1).containsKeys(101);
        assertThat(sellOrders).extractingByKey(101).asList()
                .containsExactly(
                        new Order("d", Side.SELL, 2_000, 101)
                );

        assertThat(this.orderBook.add(new Order("e", Side.SELL, 2_000, 95)))
                .hasSize(3)
                .containsExactly(
                        new Trade("e", "a", 99, 1_000),
                        new Trade("e", "c", 99, 500),
                        new Trade("e", "b", 98, 500)
                );

        buyOrders = this.orderBook.getOrders(Side.BUY);
        assertThat(buyOrders)
                .hasSize(1)
                .containsKeys(98)
                .extractingByKey(98).asList()
                .containsExactly(
                        new Order("b", Side.BUY, 700, 98)
                );

        sellOrders = this.orderBook.getOrders(Side.SELL);
        assertThat(sellOrders)
                .hasSize(1)
                .containsKeys(101)
                .extractingByKey(101).asList()
                .containsExactly(
                        new Order("d", Side.SELL, 2_000, 101)
                );
    }

    @Test
    void example2() {
        assertThat(this.orderBook.add(new Order("10000", Side.BUY, 25_500, 98))).isEmpty();
        assertThat(this.orderBook.add(new Order("10005", Side.SELL, 20_000, 105))).isEmpty();
        assertThat(this.orderBook.add(new Order("10001", Side.SELL, 500, 100))).isEmpty();
        assertThat(this.orderBook.add(new Order("10002", Side.SELL, 10_000, 100))).isEmpty();
        assertThat(this.orderBook.add(new Order("10003", Side.BUY, 50_000, 99))).isEmpty();
        assertThat(this.orderBook.add(new Order("10004", Side.SELL, 100, 103))).isEmpty();
        assertThat(this.orderBook.add(new Order("10006", Side.BUY, 16_000, 105))).hasSize(4)
                .containsExactly(
                        new Trade("10006", "10001", 100, 500),
                        new Trade("10006", "10002", 100, 10_000),
                        new Trade("10006", "10004", 103, 100),
                        new Trade("10006", "10005", 105, 5_400)
                );

        var buyOrders = this.orderBook.getOrders(Side.BUY);
        assertThat(buyOrders).hasSize(2).containsKeys(98, 99);
        assertThat(buyOrders).extractingByKey(98).asList()
                .containsExactly(
                        new Order("10000", Side.BUY, 25_500, 98)
                );
        assertThat(buyOrders).extractingByKey(99).asList()
                .containsExactly(
                        new Order("10003", Side.BUY, 50_000, 99)
                );

        var sellOrders = this.orderBook.getOrders(Side.SELL);
        assertThat(sellOrders)
                .hasSize(1)
                .containsKeys(105)
                .extractingByKey(105).asList()
                .containsExactly(
                        new Order("10005", Side.SELL, 14_600, 105)
                );
    }

    @Test
    void example3() {
        assertThat(this.orderBook.add(new Order("10000", Side.BUY, 25_500, 98))).isEmpty();
        assertThat(this.orderBook.add(new Order("10001", Side.BUY, 10_000, 97))).isEmpty();
        assertThat(this.orderBook.add(new Order("10002", Side.SELL, 20_000, 95))).hasSize(1)
                .containsExactly(
                        new Trade("10002", "10000", 98, 20_000)
                );

        var buyOrders = this.orderBook.getOrders(Side.BUY);
        assertThat(buyOrders).hasSize(2).containsKeys(98, 97);
        assertThat(buyOrders).extractingByKey(98).asList()
                .containsExactly(
                        new Order("10000", Side.BUY, 5_500, 98)
                );
        assertThat(buyOrders).extractingByKey(97).asList()
                .containsExactly(
                        new Order("10001", Side.BUY, 10_000, 97)
                );

        var sellOrders = this.orderBook.getOrders(Side.SELL);
        assertThat(sellOrders).isEmpty();
    }
}