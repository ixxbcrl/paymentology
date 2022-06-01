package com.project.paymentology.apis;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.project.paymentology.application.command.TransactionReconciliationCommandService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

@RestController
@RequestMapping(value = "/api/transaction-reconciliation")
public class TransactionReconciliationController {
    private TransactionReconciliationCommandService transactionReconciliationCommandService;

    public TransactionReconciliationController(TransactionReconciliationCommandService transactionReconciliationCommandService) {
        this.transactionReconciliationCommandService = transactionReconciliationCommandService;
    }

    @PostMapping(value = "/reconcile")
//    @ApiOperation(
//            value = "Compare Excel files",
//            tags = "Compare Excel"
//    )
    public ResponseEntity<String> fileTransactionsCompare(
            @RequestPart(value = "fileOne", required = false) MultipartFile fileOne,
            @RequestPart(value = "fileTwo", required = false) MultipartFile fileTwo) throws IOException, CsvValidationException {
//        FileInputStream file2 = new FileInputStream(fileOne);
//        Workbook workbook = new XSSFWorkbook(fileOne.getInputStream());
//        Sheet sheet = workbook.getSheetAt(0);
//        Map<Integer, List<String>> data = new HashMap<>();
//        int i = 0;
//        for (Row row : sheet) {
//            data.put(i, new ArrayList<String>());
//            for (Cell cell : row) {
//                switch (cell.getCellType()) {
//                    case STRING: data.get(i).add(cell.getRichStringCellValue().getString());
//                    case NUMERIC:
//                        if (DateUtil.isCellDateFormatted(cell)) {
//                            data.get(i).add(cell.getDateCellValue() + "");
//                        } else {
//                            data.get(i).add(cell.getNumericCellValue() + "");
//                        }
//                }
//            }
//            ++i;
//        }
//
//        System.out.println("testt: " + List.of(data));

//        List<List<String>> records = new ArrayList<List<String>>();
//        try (CSVReader csvReader = new CSVReader(new InputStreamReader(fileOne.getInputStream()));) {
//            String[] values = null;
//            while ((values = csvReader.readNext()) != null) {
//                System.out.println("thisss: " + Arrays.asList(values));
//                records.add(Arrays.asList(values));
//            }
//        }

        transactionReconciliationCommandService.reconcile(fileOne, fileTwo);

        return ResponseEntity.ok("123");
    }
}
