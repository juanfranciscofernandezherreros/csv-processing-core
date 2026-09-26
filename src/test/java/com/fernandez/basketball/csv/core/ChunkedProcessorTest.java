package com.fernandez.basketball.csv.core;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ChunkedProcessorTest {

    @Test
    void emitsFullChunksAndFinalRemainder() {
        List<List<Integer>> chunks = new ArrayList<>();

        ChunkedProcessor.process(List.of(1, 2, 3, 4, 5), 2, chunks::add);

        assertEquals(List.of(
                List.of(1, 2),
                List.of(3, 4),
                List.of(5)), chunks);
    }

    @Test
    void doesNotEmitAnythingForEmptyInput() {
        List<List<Integer>> chunks = new ArrayList<>();

        ChunkedProcessor.process(List.<Integer>of(), 500, chunks::add);

        assertEquals(List.of(), chunks);
    }

    @Test
    void rejectsInvalidChunkSize() {
        assertThrows(
                IllegalArgumentException.class,
                () -> ChunkedProcessor.process(List.of(1), 0, ignored -> {}));
    }

    @Test
    void emittedChunksAreImmutableSnapshots() {
        List<List<Integer>> chunks = new ArrayList<>();

        ChunkedProcessor.process(List.of(1, 2), 2, chunks::add);

        assertThrows(UnsupportedOperationException.class, () -> chunks.get(0).add(3));
    }
}
