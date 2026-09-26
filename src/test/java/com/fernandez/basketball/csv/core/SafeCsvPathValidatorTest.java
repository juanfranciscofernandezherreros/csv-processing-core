package com.fernandez.basketball.csv.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SafeCsvPathValidatorTest {

    @TempDir
    Path temp;

    @Test
    void acceptsReadableCsvInsideAllowedRoot() throws IOException {
        Path allowed = Files.createDirectories(temp.resolve("allowed"));
        Path csv = Files.writeString(allowed.resolve("data.csv"), "a,b\n1,2\n");

        Path validated = new SafeCsvPathValidator(allowed).validate(csv.toAbsolutePath().toString());

        assertEquals(csv.toRealPath(), validated);
    }

    @Test
    void rejectsRelativePath() throws IOException {
        Path allowed = Files.createDirectories(temp.resolve("allowed"));
        Files.writeString(allowed.resolve("data.csv"), "a,b\n");

        assertThrows(
                CsvValidationException.class,
                () -> new SafeCsvPathValidator(allowed).validate("data.csv"));
    }

    @Test
    void rejectsPathTraversalOutsideAllowedRoot() throws IOException {
        Path allowed = Files.createDirectories(temp.resolve("allowed"));
        Path outside = Files.writeString(temp.resolve("outside.csv"), "a,b\n");

        assertThrows(
                CsvValidationException.class,
                () -> new SafeCsvPathValidator(allowed)
                        .validate(allowed.resolve("..").resolve(outside.getFileName()).toAbsolutePath().toString()));
    }

    @Test
    void rejectsMissingFile() throws IOException {
        Path allowed = Files.createDirectories(temp.resolve("allowed"));

        assertThrows(
                CsvValidationException.class,
                () -> new SafeCsvPathValidator(allowed)
                        .validate(allowed.resolve("missing.csv").toAbsolutePath().toString()));
    }

    @Test
    void rejectsNonCsvFile() throws IOException {
        Path allowed = Files.createDirectories(temp.resolve("allowed"));
        Path txt = Files.writeString(allowed.resolve("data.txt"), "x");

        assertThrows(
                CsvValidationException.class,
                () -> new SafeCsvPathValidator(allowed).validate(txt.toAbsolutePath().toString()));
    }

    @Test
    void validatesExpectedFilename() throws IOException {
        Path allowed = Files.createDirectories(temp.resolve("allowed"));
        Path csv = Files.writeString(allowed.resolve("player_stats.csv"), "a,b\n");

        Path validated = new SafeCsvPathValidator(allowed)
                .validate(csv.toAbsolutePath().toString(), "player_stats.csv");

        assertEquals(csv.toRealPath(), validated);
        assertThrows(
                CsvValidationException.class,
                () -> new SafeCsvPathValidator(allowed)
                        .validate(csv.toAbsolutePath().toString(), "other.csv"));
    }

    @Test
    void rejectsSymlinkEscapingAllowedRoot() throws IOException {
        Path allowed = Files.createDirectories(temp.resolve("allowed"));
        Path outside = Files.writeString(temp.resolve("outside.csv"), "a,b\n");
        Path link = allowed.resolve("escape.csv");

        try {
            Files.createSymbolicLink(link, outside);
        } catch (UnsupportedOperationException exception) {
            return;
        }

        assertThrows(
                CsvValidationException.class,
                () -> new SafeCsvPathValidator(allowed).validate(link.toAbsolutePath().toString()));
    }
}
