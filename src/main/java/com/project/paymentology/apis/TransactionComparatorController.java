package com.project.paymentology.apis;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@RestController
@RequestMapping(value = "/api/transaction-controller")
public class TransactionComparatorController {

    @GetMapping(value = "/compare")
//    @ApiOperation(
//            value = "Compare Excel files",
//            tags = "Compare Excel"
//    )
    public ResponseEntity fileTransactionsCompare(
            @RequestPart(value = "fileOne", required = false) MultipartFile fileOne,
            @RequestPart(value = "fileTwo", required = false) MultipartFile fileTwo) throws IOException {
//        FileInputStream file2 = new FileInputStream(fileOne);
        Workbook workbook = new XSSFWorkbook(fileOne.getInputStream());
    }
}
