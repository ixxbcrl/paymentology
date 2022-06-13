package com.project.paymentology.apis;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.FileInputStream;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TransactionReconciliationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void should_return_valid_reconcile_data_status() throws Exception {
        Path path = Path.of("").toAbsolutePath();
        String stringPath = path + "\\src\\test\\java\\com\\project\\paymentology\\apis";
        MockMultipartFile firstFile = new MockMultipartFile("fileOne", new FileInputStream(stringPath + "\\ClientMarkoffFile20140113.csv"));
        MockMultipartFile secondFile = new MockMultipartFile("fileTwo", new FileInputStream(stringPath + "\\PaymentologyMarkoffFile20140113.csv"));

        this.mockMvc.perform(MockMvcRequestBuilders.multipart("/api/transaction-reconciliation/reconcile")
                        .file(firstFile)
                        .file(secondFile))
                .andExpect(status().is(200));
    }

    @Test
    public void should_return_valid_reconcile_data() throws Exception {
        Path path = Path.of("").toAbsolutePath();
        String stringPath = path + "\\src\\test\\java\\com\\project\\paymentology\\apis";
        MockMultipartFile firstFile = new MockMultipartFile("fileOne", new FileInputStream(stringPath + "\\ClientMarkoffFile20140113.csv"));
        MockMultipartFile secondFile = new MockMultipartFile("fileTwo", new FileInputStream(stringPath + "\\PaymentologyMarkoffFile20140113.csv"));

        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.multipart("/api/transaction-reconciliation/reconcile")
                        .file(firstFile)
                        .file(secondFile))
                .andExpect(status().is(200)).andReturn();

        String jsonBody = result.getResponse().getContentAsString();
        JSONObject body = new JSONObject(jsonBody);
        assertEquals(body.getString("fileOneTotalRecords"), String.valueOf(4));
        assertEquals(body.getString("fileTwoTotalRecords"), String.valueOf(4));
        assertEquals(body.getString("matchingRecords"), String.valueOf(4));
        assertEquals(body.getString("fileOneUnmatchedRecords"), String.valueOf(0));
        assertEquals(body.getString("fileTwoUnmatchedRecords"), String.valueOf(0));
        assertEquals(body.getJSONArray("transactionReconcileResponseDto").length(), 0);
    }

    @Test
    public void should_throw_error_with_invalid_data() throws Exception {
        Path path = Path.of("").toAbsolutePath();
        String stringPath = path + "\\src\\test\\java\\com\\project\\paymentology\\apis";
        MockMultipartFile firstFile = new MockMultipartFile("fileOne", new FileInputStream(stringPath + "\\ClientMarkoffFile20140113-error.csv"));
        MockMultipartFile secondFile = new MockMultipartFile("fileTwo", new FileInputStream(stringPath + "\\PaymentologyMarkoffFile20140113.csv"));

        Exception exception = assertThrows(Exception.class, () -> this.mockMvc.perform(MockMvcRequestBuilders.multipart("/api/transaction-reconciliation/reconcile")
                        .file(firstFile)
                        .file(secondFile)));
        assertTrue(exception.getMessage().contains("Validations failed for"));
    }

    @Test
    public void should_throw_error_with_missing_file() throws Exception {
        Path path = Path.of("").toAbsolutePath();
        String stringPath = path + "\\src\\test\\java\\com\\project\\paymentology\\apis";
        MockMultipartFile firstFile = new MockMultipartFile("fileOne", new FileInputStream(stringPath + "\\ClientMarkoffFile20140113-error.csv"));
        MockMultipartFile secondFile = null;

        assertThrows(IllegalArgumentException.class, () -> this.mockMvc.perform(MockMvcRequestBuilders.multipart("/api/transaction-reconciliation/reconcile")
                .file(firstFile)
                .file(secondFile)));
    }
}
