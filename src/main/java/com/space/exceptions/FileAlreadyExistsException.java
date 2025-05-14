package com.space.exceptions;

public class FileAlreadyExistsException extends Exception {
    public FileAlreadyExistsException(String message) {
        super(message);
    }
}
