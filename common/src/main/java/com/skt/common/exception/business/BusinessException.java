package com.skt.common.exception.business;

import com.skt.common.exception.SKTException;

public class BusinessException extends SKTException {
    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
