package com.fernandez.basketball.csv.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public final class ChunkedProcessor {

    private ChunkedProcessor() {
    }

    public static <T> void process(
            Iterable<T> items,
            int chunkSize,
            Consumer<List<T>> chunkConsumer) {

        Objects.requireNonNull(items, "items cannot be null");
        Objects.requireNonNull(chunkConsumer, "chunkConsumer cannot be null");
        if (chunkSize <= 0) {
            throw new IllegalArgumentException("chunkSize must be greater than zero");
        }

        List<T> chunk = new ArrayList<>(chunkSize);
        for (T item : items) {
            chunk.add(item);
            if (chunk.size() == chunkSize) {
                chunkConsumer.accept(List.copyOf(chunk));
                chunk.clear();
            }
        }
        if (!chunk.isEmpty()) {
            chunkConsumer.accept(List.copyOf(chunk));
        }
    }
}
