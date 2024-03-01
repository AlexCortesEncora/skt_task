package com.skt.common.exception;

public class SKTException extends RuntimeException {

    public SKTException(String message) {
        super(message);
    }

    public SKTException(String message, Throwable cause) {
        super(message, cause);
    }
}
