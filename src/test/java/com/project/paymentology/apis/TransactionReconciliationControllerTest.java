package com.project.paymentology.apis;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.FileInputStream;
import java.nio.file.Path;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TransactionReconciliationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void should_return_valid_weighted_compare_lists() throws Exception {
        Path path = Path.of("").toAbsolutePath();
        String stringPath = path + "\\src\\test\\java\\com\\project\\paymentology\\apis";
        MockMultipartFile firstFile = new MockMultipartFile("fileOne", new FileInputStream(stringPath + "\\ClientMarkoffFile20140113.csv"));
        MockMultipartFile secondFile = new MockMultipartFile("fileTwo", new FileInputStream(stringPath + "\\PaymentologyMarkoffFile20140113.csv"));

        this.mockMvc.perform(MockMvcRequestBuilders.multipart("/api/transaction-reconciliation/reconcile")
                        .file(firstFile)
                        .file(secondFile))
                .andExpect(status().is(200));
    }
}
