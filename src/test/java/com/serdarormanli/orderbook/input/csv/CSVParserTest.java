package com.serdarormanli.orderbook.input.csv;

import com.serdarormanli.orderbook.input.CSVParser;
import com.serdarormanli.orderbook.model.Order;
import com.serdarormanli.orderbook.model.Side;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.LinkedList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CSVParserTest {

    private com.serdarormanli.orderbook.input.CSVParser CSVParser;

    @BeforeEach
    void setUp() {
        this.CSVParser = new CSVParser();
    }

    @Test
    void parseSuccessful() {
        String content = """
                10000,B,98,25500
                10005,S,105,20000
                10001,S,100,500
                10002,S,100,10000
                10003,B,99,50000
                10004,S,103,100
                """;
        var input = new StringReader(content);

        var orders = new LinkedList<Order>();

        this.CSVParser.parse(input, orders);

        assertThat(orders).containsExactly(
                new Order("10000", Side.BUY, 25_500, 98),
                new Order("10005", Side.SELL, 20_000, 105),
                new Order("10001", Side.SELL, 500, 100),
                new Order("10002", Side.SELL, 10_000, 100),
                new Order("10003", Side.BUY, 50_000, 99),
                new Order("10004", Side.SELL, 100, 103)
        );
    }

    @Test
    void failUnknownSide() {
        String content = """
                10000,C,98,25500
                """;
        var input = new StringReader(content);

        assertThatThrownBy(() -> this.CSVParser.parse(input, new LinkedList<>()))
                .hasMessageContaining("CSV parsing failed at line 1")
                .hasRootCauseMessage("Unknown side C");
    }

    @Test
    void failDoublePrice() {
        String content = """
                10000,S,98,10000
                10001,B,98.1,25500
                """;
        var input = new StringReader(content);

        assertThatThrownBy(() -> this.CSVParser.parse(input, new LinkedList<>()))
                .hasMessageContaining("CSV parsing failed at line 2")
                .hasRootCauseMessage("For input string: \"98.1\"");
    }

    @Test
    void parseEmpty() {
        var input = new StringReader("");

        var orders = new LinkedList<Order>();

        this.CSVParser.parse(input, orders);

        assertThat(orders).isEmpty();
    }
}
