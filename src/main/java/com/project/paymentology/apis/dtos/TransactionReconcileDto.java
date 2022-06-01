package com.project.paymentology.apis.dtos;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
public class TransactionReconcileDto {
    private String profileName;

    private LocalDateTime transactionDate;

    private double transactionAmount;

    private String transactionNarrative;

    private String transactionDescription;

    private String transactionID;

    private int transactionType;

    private String walletReference;
}
