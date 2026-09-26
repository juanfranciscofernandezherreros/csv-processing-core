package com.fernandez.basketball.csv.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Objects;

public final class SafeCsvPathValidator {

    private final Path allowedRoot;

    public SafeCsvPathValidator(String allowedRoot) {
        this(Path.of(Objects.requireNonNull(allowedRoot, "allowedRoot cannot be null")));
    }

    public SafeCsvPathValidator(Path allowedRoot) {
        this.allowedRoot = Objects.requireNonNull(allowedRoot, "allowedRoot cannot be null");
    }

    public Path validate(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            throw new CsvValidationException("CSV path cannot be blank");
        }

        final Path requested;
        try {
            requested = Path.of(filePath);
        } catch (InvalidPathException exception) {
            throw new CsvValidationException("Invalid CSV path: " + filePath, exception);
        }

        if (!requested.isAbsolute()) {
            throw new CsvValidationException("CSV path must be absolute: " + filePath);
        }

        if (!hasCsvExtension(requested)) {
            throw new CsvValidationException("Expected a .csv file: " + filePath);
        }

        try {
            Path rootReal = allowedRoot.toAbsolutePath().normalize().toRealPath();
            Path fileReal = requested.normalize().toRealPath();

            if (!fileReal.startsWith(rootReal)) {
                throw new CsvValidationException("CSV path is outside the allowed root: " + filePath);
            }
            if (!Files.isRegularFile(fileReal)) {
                throw new CsvValidationException("CSV path is not a regular file: " + fileReal);
            }
            if (!Files.isReadable(fileReal)) {
                throw new CsvValidationException("CSV file is not readable: " + fileReal);
            }
            return fileReal;
        } catch (IOException exception) {
            throw new CsvValidationException("CSV path cannot be resolved: " + filePath, exception);
        }
    }

    public Path validate(String filePath, String expectedFilename) {
        Path validated = validate(filePath);
        if (expectedFilename == null || expectedFilename.isBlank()) {
            throw new CsvValidationException("Expected CSV filename cannot be blank");
        }
        if (validated.getFileName() == null
                || !expectedFilename.equalsIgnoreCase(validated.getFileName().toString())) {
            throw new CsvValidationException(
                    "Expected CSV filename " + expectedFilename + " but got " + validated.getFileName());
        }
        return validated;
    }

    private boolean hasCsvExtension(Path path) {
        Path filename = path.getFileName();
        return filename != null
                && filename.toString().toLowerCase(Locale.ROOT).endsWith(".csv");
    }
}
