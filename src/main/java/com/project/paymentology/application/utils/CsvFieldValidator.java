package com.project.paymentology.application.utils;

import com.project.paymentology.apis.common.ErrorResult;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.project.paymentology.apis.common.ErrorCode.DATA_FIELD_VALIDATION_EXCEPTION;

public class CsvFieldValidator {
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static CsvFieldValidator getInstance() {
        return new CsvFieldValidator();
    }

    public void validateLocalDateTimeFormat(String localDateTime, long currentLineNumber, List<ErrorResult> errorResultList) {
        try {
            LocalDateTime.parse(localDateTime, DATE_FORMATTER);
        } catch (Exception e) {
            if (errorResultList != null) {
                errorResultList.add(new ErrorResult(DATA_FIELD_VALIDATION_EXCEPTION, String.format("Unable to parse date time in line %d", currentLineNumber)));
            }
        }
    }

    public void validateDouble(String doubleString, long currentLineNumber, List<ErrorResult> errorResultList) {
        try {
            Double.parseDouble(doubleString);
        } catch (Exception e) {
            if (errorResultList != null) {
                errorResultList.add(new ErrorResult(DATA_FIELD_VALIDATION_EXCEPTION, "Unable to parse date time"));
            }
        }
    }
}
