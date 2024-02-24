package com.skt.common.exception.external;

public class ThirdPartyServiceException extends InfrastructureException {
    public ThirdPartyServiceException(String message) {
        super(message);
    }

    public ThirdPartyServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
