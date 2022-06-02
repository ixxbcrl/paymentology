package com.project.paymentology.application.command;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import com.project.paymentology.apis.dtos.TransactionReconcileDto;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class TransactionReconciliationCommandService {

    public Map<String, List<String>> reconcile(MultipartFile fileOne, MultipartFile fileTwo) {
        List<TransactionReconcileDto> reconciledResult = new ArrayList<>();
        List<TransactionReconcileDto> unmatchedFileOne1 = new ArrayList<>();
        List<TransactionReconcileDto> unmatchedFileTwo1 = new ArrayList<>();
        Map<String, List<TransactionReconcileDto>> parsedDtoMapFileOne = new HashMap<>();
        Map<String, List<TransactionReconcileDto>> parsedDtoMapFileTwo = new HashMap<>();

        parsedDtoMapFileOne = parseFileToMap(fileOne);
        parsedDtoMapFileTwo = parseFileToMap(fileTwo);
        reconcileParsedFiles(parsedDtoMapFileOne, parsedDtoMapFileTwo);

        System.out.println("final map1 size: " + parsedDtoMapFileOne.size());
        System.out.println("final map2 size: " + parsedDtoMapFileTwo.size());

        //print one
        for (Map.Entry<String, List<TransactionReconcileDto>> lst : parsedDtoMapFileOne.entrySet()) {
            String key = lst.getKey();
            List<TransactionReconcileDto> lstValue = lst.getValue();
            if (lstValue.size() > 1) {
                System.out.println("YOYOYOYOYOYO");
            }

            for (TransactionReconcileDto val : lstValue) {
                System.out.println("Key mapOne1: " + key);
                System.out.println("mapOne1 val: " + val);
            }
        }

        //print two
        for (Map.Entry<String, List<TransactionReconcileDto>> lst : parsedDtoMapFileTwo.entrySet()) {
            String key = lst.getKey();
            List<TransactionReconcileDto> lstValue = lst.getValue();
            if (lstValue.size() > 1) {
                System.out.println("YOYOYOYOYOYO");
            }

            for (TransactionReconcileDto val : lstValue) {
                System.out.println("Key mapTwo1: " + key);
                System.out.println("mapTwo1 val: " + val);
            }
        }
        return new HashMap<>();
    }

    private Map<String, List<TransactionReconcileDto>> parseFileToMap(MultipartFile file) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Map<String, List<TransactionReconcileDto>> parsedDtoMap = new HashMap<>();
        String[] values;

        try {
            CSVParser parser = new CSVParserBuilder()
                    .withSeparator(',')
                    .withIgnoreQuotations(true)
                    .build();

            CSVReader csvReader = new CSVReaderBuilder(new InputStreamReader(file.getInputStream()))
                    .withSkipLines(1)
                    .withCSVParser(parser)
                    .build();

            while ((values = csvReader.readNext()) != null) {
                List<String> cur = Arrays.asList(values);
                TransactionReconcileDto dto = TransactionReconcileDto.builder()
                        .transactionDate(LocalDateTime.parse(cur.get(1), formatter))
                        .transactionAmount(Double.parseDouble(cur.get(2)))
                        .transactionNarrative(cur.get(3))
                        .transactionDescription(cur.get(4))
                        .transactionID(cur.get(5))
                        .transactionType(Integer.parseInt(cur.get(6)))
                        .walletReference(cur.get(7))
                        .build();
                parsedDtoMap.putIfAbsent(dto.getTransactionID(), new ArrayList<>());
                parsedDtoMap.get(dto.getTransactionID()).add(dto);
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }

        return parsedDtoMap;
    }

    private void reconcileParsedFiles(Map<String, List<TransactionReconcileDto>> mapOne,
                                      Map<String, List<TransactionReconcileDto>> mapTwo) {
        Iterator<Map.Entry<String, List<TransactionReconcileDto>>> mapOneEntrySet = mapOne.entrySet().iterator();
        while (mapOneEntrySet.hasNext()) {
            Map.Entry<String, List<TransactionReconcileDto>> mapOneEntry = mapOneEntrySet.next();
            List<TransactionReconcileDto> mapOneEntryValue = mapOneEntry.getValue();
            if (mapTwo.get(mapOneEntry.getKey()) != null) {
                List<TransactionReconcileDto> mapTwoEntryValue = mapTwo.get(mapOneEntry.getKey());
                if (mapOneEntryValue.size() > 1) {
//                    for (Iterator<TransactionReconcileDto> entryOneItr = mapOneEntryValue.iterator(); entryOneItr.hasNext();) {
                    for (int i=0; i<mapOneEntryValue.size(); ++i) {
                        int highestWeight = 0;

//                        for (Iterator<TransactionReconcileDto> entryTwoItr = mapTwoEntryValue.iterator(); entryTwoItr.hasNext();) {
//                            TransactionReconcileDto entryOne = entryOneItr.next();
//                            TransactionReconcileDto entryTwo = entryTwoItr.next();
//
//                            //If the objects are exactly the same, we remove them
//                            if (entryOne.weightedCompare(entryTwo) == 0) {
//                                entryOneItr.remove();
//                                entryTwoItr.remove();
//                            } else if (entryOne.weightedCompare(entryTwo))
                        }
                    }
                }
//                if (item.getValue().size() > 1) {
//                    reconciledResult.addAll(item.getValue());
//                } else {
//                    reconciledResult.add(item.getValue().get(0));
//                }
                mapTwo.remove(mapOneEntry.getKey());
                mapOneEntrySet.remove();
            }
//            else {
//                unmatchedFileOne1.addAll(item.getValue());
//            }
        }
    }

    /**
     * We rank each property based on a simple weighted scale as follows:
     * WalletReference - 6
     * TransactionAmount - 5
     * TransactionDate - 3
     * TransactionType - 3
     * TransactionDescription - 3
     * TransactionNarrative - 1
     */
    private int reconcileRow(TransactionReconcileDto listOne, TransactionReconcileDto listTwo) {
    }
}
