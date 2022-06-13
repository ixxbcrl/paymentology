package com.project.paymentology.apis.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionReconcileSummaryDto {
    private long fileOneTotalRecords;
    private long fileTwoTotalRecords;
    private int matchingRecords;
    private int fileOneUnmatchedRecords;
    private int fileTwoUnmatchedRecords;
    private List<TransactionReconcileResponseDto> transactionReconcileResponseDto;

    public void incrementFileOneTotalRecords() {
        ++fileOneTotalRecords;
    }

    public void incrementFileTwoTotalRecords() {
        ++fileTwoTotalRecords;
    }

    public void incrementMatchingRecords() {
        ++matchingRecords;
    }

    public void incrementFileOneUnmatchedRecords() {
        ++fileOneUnmatchedRecords;
    }

    public void incrementFileTwoUnmatchedRecords() {
        ++fileTwoUnmatchedRecords;
    }
}
