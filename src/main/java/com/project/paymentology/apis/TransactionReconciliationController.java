package com.project.paymentology.apis;

import com.project.paymentology.apis.dtos.TransactionReconcileResponseDto;
import com.project.paymentology.application.command.TransactionReconciliationCommandService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(value = "/api/transaction-reconciliation")
public class TransactionReconciliationController {
    private TransactionReconciliationCommandService transactionReconciliationCommandService;

    public TransactionReconciliationController(TransactionReconciliationCommandService transactionReconciliationCommandService) {
        this.transactionReconciliationCommandService = transactionReconciliationCommandService;
    }

    @PostMapping(value = "/reconcile")
    public ResponseEntity<List<TransactionReconcileResponseDto>> fileTransactionsCompare(
            @RequestPart(value = "fileOne", required = false) MultipartFile fileOne,
            @RequestPart(value = "fileTwo", required = false) MultipartFile fileTwo) {
        return ResponseEntity.ok(transactionReconciliationCommandService.reconcile(fileOne, fileTwo));
    }
}
