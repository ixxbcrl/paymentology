package com.project.paymentology.application.command;

import com.project.paymentology.apis.dtos.TransactionReconcileSummaryDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionReconciliationCommandServiceTest {

    @InjectMocks
    TransactionReconciliationCommandService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void should_return_valid_summary_reconcile_data() throws IOException {
        Path path = Path.of("").toAbsolutePath();
        String stringPath = path + "\\src\\test\\java\\com\\project\\paymentology\\apis";
        MockMultipartFile firstFile = new MockMultipartFile("fileOne", new FileInputStream(stringPath + "\\ClientMarkoffFile20140113.csv"));
        MockMultipartFile secondFile = new MockMultipartFile("fileTwo", new FileInputStream(stringPath + "\\PaymentologyMarkoffFile20140113.csv"));
        TransactionReconcileSummaryDto dto = service.reconcile(firstFile, secondFile);

        assertEquals(4, dto.getFileOneTotalRecords());
        assertEquals(4, dto.getFileTwoTotalRecords());
        assertEquals(4, dto.getMatchingRecords());
        assertEquals(0, dto.getFileOneUnmatchedRecords());
        assertEquals(0, dto.getFileTwoUnmatchedRecords());
    }

    @Test
    void should_return_valid_summary_reconcile_data_with_unmatched() throws IOException {
        Path path = Path.of("").toAbsolutePath();
        String stringPath = path + "\\src\\test\\java\\com\\project\\paymentology\\apis";
        MockMultipartFile firstFile = new MockMultipartFile("fileOne", new FileInputStream(stringPath + "\\ClientMarkoffFile20140113-unmatched.csv"));
        MockMultipartFile secondFile = new MockMultipartFile("fileTwo", new FileInputStream(stringPath + "\\PaymentologyMarkoffFile20140113.csv"));
        TransactionReconcileSummaryDto dto = service.reconcile(firstFile, secondFile);

        assertEquals(4, dto.getFileOneTotalRecords());
        assertEquals(4, dto.getFileTwoTotalRecords());
        assertEquals(3, dto.getMatchingRecords());
        assertEquals(1, dto.getFileOneUnmatchedRecords());
        assertEquals(1, dto.getFileTwoUnmatchedRecords());
    }
}
