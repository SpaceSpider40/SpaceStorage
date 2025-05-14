package com.space.exceptions;

public class UnexpectedParameterException extends Exception {
    public UnexpectedParameterException() {
        super("Expected command, got parameter");
    }
}
