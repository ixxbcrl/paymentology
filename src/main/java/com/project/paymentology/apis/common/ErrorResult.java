package com.project.paymentology.apis.common;

public class ErrorResult {

    private String errorCode;

    private String message;

    public ErrorResult(ErrorCode errorCode) {
        this.errorCode = errorCode.getValue();
    }

    public ErrorResult(ErrorCode errorCode, String message) {
        this(errorCode);
        this.message= message;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public String getMessage() {
        return this.message;
    }
}
