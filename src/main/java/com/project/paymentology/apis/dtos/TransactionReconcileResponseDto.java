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
public class TransactionReconcileResponseDto {

    public String sourceFile;

    public TransactionReconcileDto unmatchedTransaction;

    public TransactionReconcileDto exactMatchTransaction;

    public List<TransactionReconcileDto> closeMatches;
}
