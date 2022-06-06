package com.project.paymentology.application.exception;

import com.project.paymentology.apis.common.ErrorCode;

public class WebServiceException extends RuntimeException {
    private final ErrorCode errorCode;

    public WebServiceException(String message) {
        super(message);
        this.errorCode = ErrorCode.UNKNOWN;
    }

    public WebServiceException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
