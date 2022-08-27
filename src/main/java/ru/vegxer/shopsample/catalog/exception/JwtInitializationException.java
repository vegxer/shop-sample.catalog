package ru.vegxer.shopsample.catalog.exception;

public class JwtInitializationException extends RuntimeException {
    public JwtInitializationException() {
        super();
    }

    public JwtInitializationException(Throwable e) {
        super("Something went wong while reading public key!", e);
    }
}
