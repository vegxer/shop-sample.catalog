package ru.vegxer.shopsample.catalog.exception;

public class StorageException extends RuntimeException {
    public StorageException() {
        super();
    }

    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Exception cause) {
        super(message, cause);
    }
}
