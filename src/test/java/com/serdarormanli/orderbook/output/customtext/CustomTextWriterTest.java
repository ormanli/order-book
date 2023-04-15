package com.serdarormanli.orderbook.output.customtext;

import com.serdarormanli.orderbook.model.Order;
import com.serdarormanli.orderbook.model.Side;
import com.serdarormanli.orderbook.model.Trade;
import com.serdarormanli.orderbook.output.CustomTextWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.assertj.core.api.Assertions.assertThat;

public class CustomTextWriterTest {

    private CustomTextWriter customTextWriter;
    private ByteArrayOutputStream byteArrayOutputStream;
    private OutputStreamWriter outputStreamWriter;
    private Map<Integer, List<Order>> bids;
    private Map<Integer, List<Order>> asks;
    private List<Trade> trades;

    @BeforeEach
    void setUp() {
        this.byteArrayOutputStream = new ByteArrayOutputStream();
        this.outputStreamWriter = new OutputStreamWriter(this.byteArrayOutputStream, Charset.defaultCharset());

        this.customTextWriter = new CustomTextWriter();

        this.bids = new TreeMap<>(Comparator.reverseOrder());
        this.bids.put(98, List.of(
                new Order("A", Side.BUY, 123_456_789, 98),
                new Order("B", Side.BUY, 100_000, 98)
        ));
        this.bids.put(99, List.of(new Order("C", Side.BUY, 50_000, 99)));

        this.asks = new TreeMap<>();
        this.asks.put(100, List.of(
                new Order("D", Side.SELL, 500, 100),
                new Order("E", Side.SELL, 100_000, 100)
        ));
        this.asks.put(103, List.of(new Order("F", Side.SELL, 100, 103)));
        this.asks.put(105, List.of(new Order("G", Side.SELL, 200_000, 123456)));

        this.trades = List.of(
                new Trade("10006", "10001", 100, 500),
                new Trade("10006", "10002", 100, 10_000)
        );
    }

    @Test
    void writeEverything() {
        this.customTextWriter.write(this.outputStreamWriter, this.trades);
        this.customTextWriter.write(this.outputStreamWriter, this.bids, this.asks);

        assertThat(this.byteArrayOutputStream).asString()
                .isEqualTo("""
                        trade 10006,10001,100,500
                        trade 10006,10002,100,10000
                             50,000     99 |    100         500
                        123,456,789     98 |    100     100,000
                            100,000     98 |    103         100
                                           | 123456     200,000         
                        """);
    }

    @Test
    void writeOnlyTrades() {
        this.customTextWriter.write(this.outputStreamWriter, this.trades);

        assertThat(this.byteArrayOutputStream).asString()
                .isEqualTo("""
                        trade 10006,10001,100,500
                        trade 10006,10002,100,10000 
                        """);
    }

    @Test
    void writeAsksAndBids() {
        this.customTextWriter.write(this.outputStreamWriter, this.bids, this.asks);

        assertThat(this.byteArrayOutputStream).asString()
                .isEqualTo("""
                             50,000     99 |    100         500
                        123,456,789     98 |    100     100,000
                            100,000     98 |    103         100
                                           | 123456     200,000         
                        """);
    }

    @Test
    void writeOnlyAsks() {
        this.customTextWriter.write(this.outputStreamWriter, Collections.emptyMap(), this.asks);

        assertThat(this.byteArrayOutputStream).asString()
                .isEqualTo("""
                                           |    100         500
                                           |    100     100,000
                                           |    103         100
                                           | 123456     200,000         
                        """);
    }

    @Test
    void writeOnlyBids() {
        this.customTextWriter.write(this.outputStreamWriter, this.bids, Collections.emptyMap());

        assertThat(this.byteArrayOutputStream).asString()
                .isEqualTo("""
                             50,000     99 |                  \s
                        123,456,789     98 |                  \s
                            100,000     98 |                  \s
                        """);
    }

    @Test
    void writeNothing() {
        this.customTextWriter.write(this.outputStreamWriter, Collections.emptyMap(), Collections.emptyMap());

        assertThat(this.byteArrayOutputStream).asString().isEqualTo("");
    }
}