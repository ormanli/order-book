package com.serdarormanli.orderbook.input;

import com.serdarormanli.orderbook.model.Order;
import com.serdarormanli.orderbook.model.Side;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.apache.commons.csv.CSVFormat;

import java.io.IOException;
import java.io.Reader;
import java.util.Queue;

public class CSVParser implements Parser {

    @Override
    @SneakyThrows(IOException.class)
    public void parse(@NonNull Reader reader, @NonNull Queue<Order> queue) {
        var parser = new org.apache.commons.csv.CSVParser(reader, CSVFormat.DEFAULT);

        for (var record : parser) {
            var id = record.get(0);
            var sideAsString = record.get(1);
            var quantityAsString = record.get(2);
            var priceAsString = record.get(3);

            try {
                var order = new Order(id, this.extractSide(sideAsString), Integer.parseInt(priceAsString), Integer.parseInt(quantityAsString));
                queue.add(order);
            } catch (Exception e) {
                throw new ParseException("CSV parsing failed at line %d".formatted(record.getRecordNumber()), e);
            }
        }
    }

    private Side extractSide(String s) {
        return switch (s) {
            case "S" -> Side.SELL;
            case "B" -> Side.BUY;
            default -> throw new IllegalArgumentException("Unknown side %s".formatted(s));
        };
    }
}
