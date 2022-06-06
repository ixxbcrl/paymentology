package com.project.paymentology.application.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.when;

public class TransactionReconciliationCommandServiceTest {

    @InjectMocks
    TransactionReconciliationCommandService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void should_return_valid_weighted_compare_lists() {
//        when(service.reconcile())
    }
}
