package com.project.paymentology.apis.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionReconcileDto {

    private LocalDateTime transactionDate;

    private long transactionAmount;

    private String transactionNarrative;

    private String transactionDescription;

    private String transactionID;

    private int transactionType;

    private String walletReference;

    private long lineNumber;

    //to store the weightedCompare score
    public int weightedCompare;

    /**
     * We rank each property based on a simple weighted scale as follows:
     * WalletReference - 6
     * TransactionAmount - 5
     * TransactionDate - 3
     * TransactionType - 3
     * TransactionDescription - 3
     * TransactionNarrative - 1
     * Cumulative weighted value = 21
     */
    public int weightedCompare(TransactionReconcileDto toCompare) {
        int total=0;
        if (this.walletReference.equals(toCompare.walletReference)) total+=6;
        if (this.transactionAmount == toCompare.getTransactionAmount()) total+=5;
        if (this.transactionDate.equals(toCompare.getTransactionDate())) total+=3;
        if (this.transactionDescription.equals(toCompare.transactionDescription)) total+=3;
        if (this.transactionType == toCompare.getTransactionType()) total+=3;
        if (this.transactionNarrative.equals(toCompare.getTransactionNarrative())) total+=1;

        return total;
    }
}
