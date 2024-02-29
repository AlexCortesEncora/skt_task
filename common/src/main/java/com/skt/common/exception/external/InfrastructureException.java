package com.skt.common.exception.external;

import com.skt.common.exception.SKTException;

public class InfrastructureException extends SKTException {

    public InfrastructureException(String message) {
        super(message);
    }

    public InfrastructureException(String message, Throwable cause) {
        super(message, cause);
    }
}
