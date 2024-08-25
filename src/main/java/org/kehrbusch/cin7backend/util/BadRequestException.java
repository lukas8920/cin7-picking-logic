package org.kehrbusch.cin7backend.util;

public class BadRequestException extends Exception {
    public BadRequestException(String message) {
        super(message);
    }
}
