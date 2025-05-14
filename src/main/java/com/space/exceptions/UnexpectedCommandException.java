package com.space.exceptions;

public class UnexpectedCommandException extends Exception {
    public UnexpectedCommandException() {
        super("Expected an parameter, got command instead");
    }
}
