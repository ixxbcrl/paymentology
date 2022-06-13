package com.project.paymentology.apis.common;

public enum ErrorCode {
    UNKNOWN("unknown_error"),
    FILE_OPERATION_FAILED("file_operation_failed"),
    INVALID_FILES("invalid_files"),
    DATA_FIELD_VALIDATION_EXCEPTION("data_field_validation_exception");

    private final String value;

    ErrorCode(String errCode) {
        this.value = errCode;
    }

    public String getValue() {
        return value;
    }
}
