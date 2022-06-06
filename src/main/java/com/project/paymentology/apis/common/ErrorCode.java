package com.project.paymentology.apis.common;

public enum ErrorCode {
    UNKNOWN("unknown_error"),
    FILE_OPERATION_FAILED("file_operation_failed");

    private final String value;

    ErrorCode(String errCode) {
        this.value = errCode;
    }

    public String getValue() {
        return value;
    }
}
