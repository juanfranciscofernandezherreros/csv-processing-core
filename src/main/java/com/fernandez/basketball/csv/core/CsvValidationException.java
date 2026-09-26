package com.fernandez.basketball.csv.core;

public class CsvValidationException extends CsvProcessingException {

    public CsvValidationException(String message) {
        super(message);
    }

    public CsvValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
