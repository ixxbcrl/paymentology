package com.project.paymentology.application.command;

import com.opencsv.*;
import com.opencsv.exceptions.CsvValidationException;
import com.project.paymentology.apis.dtos.TransactionReconcileDto;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TransactionReconciliationCommandService {

    public Map<String, List<String>> reconcile(MultipartFile fileOne, MultipartFile fileTwo) {
        List<List<String>> result = new ArrayList<>();
        List<String> unmatchedFileOne = new ArrayList<>();
        List<String> unmatchedFileTwo = new ArrayList<>();
        Map<String, List<List<String>>> mapOne = new HashMap<>();
        Map<String, List<List<String>>> mapTwo = new HashMap<>();
        int total = 0;
        List<TransactionReconcileDto> reconciledResult = new ArrayList<>();
        List<TransactionReconcileDto> unmatchedFileOne1 = new ArrayList<>();
        List<TransactionReconcileDto> unmatchedFileTwo1 = new ArrayList<>();
        Map<String, List<TransactionReconcileDto>> mapOne1 = new HashMap<>();
        Map<String, List<TransactionReconcileDto>> mapTwo1 = new HashMap<>();

        try {
            String[] values;
            CSVParser parser = new CSVParserBuilder()
                    .withSeparator(',')
                    .withIgnoreQuotations(true)
                    .build();

            CSVReader csvReader = new CSVReaderBuilder(new InputStreamReader(fileOne.getInputStream()))
                    .withSkipLines(1)
                    .withCSVParser(parser)
                    .build();
            CSVReader csvReaderTwo = new CSVReaderBuilder(new InputStreamReader(fileTwo.getInputStream()))
                    .withSkipLines(1)
                    .withCSVParser(parser)
                    .build();

            //Parsing first file
            while ((values = csvReader.readNext()) != null) {
                List<String> cur = Arrays.asList(values);
                TransactionReconcileDto dto = TransactionReconcileDto.builder()
                        .profileName(cur.get(0))
                        .transactionDate(LocalDateTime.parse(cur.get(1)))
                        .transactionAmount(Double.parseDouble(cur.get(2)))
                        .transactionNarrative(cur.get(3))
                        .transactionDescription(cur.get(4))
                        .transactionID(cur.get(5))
                        .transactionType(Integer.parseInt(cur.get(6)))
                        .walletReference(cur.get(7))
                        .build();
//                mapOne.putIfAbsent(cur.get(5), new ArrayList<>());
//                mapOne.get(cur.get(5)).add(cur);
                mapOne1.putIfAbsent(dto.getTransactionID(), new ArrayList<>());
                mapOne1.get(dto.getTransactionID()).add(dto);
                ++total;
            }

            //Parsing second file
            while ((values = csvReaderTwo.readNext()) != null) {
                List<String> cur = Arrays.asList(values);
                mapTwo.putIfAbsent(cur.get(5), new ArrayList<>());
                mapTwo.get(cur.get(5)).add(cur);
            }

            Iterator<Map.Entry<String, List<List<String>>>> it = mapOne.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, List<List<String>>> item = it.next();
//            for (String keyOne : mapOne.keySet()) {
                if (mapTwo.get(item.getKey()) != null) {
                    if (result.size() > 1) {
                        result.add(mapOne.get(item.getKey())
                                .stream()
                                .flatMap(Collection::stream)
                                .collect(Collectors.toList()));
                    } else {
                        result.add(mapOne.get(item.getKey()).get(0));
                    }
                    mapTwo.remove(item.getKey());
//                    mapOne.remove(keyOne);
                    it.remove();
                } else {
                    unmatchedFileOne.addAll(item.getValue()
                            .stream()
                            .flatMap(Collection::stream)
                            .collect(Collectors.toList()));
                }
//            }
            }

//            for (List<String> lst : result) {
//                System.out.println("Matchedd: " + lst.get(5));
//            }
            for (Map.Entry<String, List<List<String>>> lst : mapTwo.entrySet()) {
                unmatchedFileTwo.addAll(lst.getValue()
                        .stream()
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList()));
            }
            System.out.println("final map1 size: " + mapOne.size());
            System.out.println("final map2 size: " + mapTwo.size());

//            for (Map.Entry<String, List<List<String>>> lst : mapOne.entrySet()) {
//                String key = lst.getKey();
//                List<List<String>> val = lst.getValue();
//                if (val.size() > 1) {
//                    System.out.println("YOYOYOYOYOYO");
//                }
//
//                 for (List<String> it : val) {
//                     System.out.println("Key is: " + key);
//                     System.out.println("tizz val: " + it);
//                 }
//            }
//            System.out.println("totalll: " + total);

        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }

//        try (CSVReader csvReader = new CSVReader(new InputStreamReader(fileOne.getInputStream()))) {
//            String[] values;
//            int firstRow = 0;
//
//            while ((values = csvReader.readNext()) != null) {
//                records.add(Arrays.asList(values));
//                record.put()
//            }
//        } catch (IOException | CsvValidationException e) {
//            System.out.println("Error parsing input file(s)");
//            e.printStackTrace();
//        }
        return new HashMap<>();
    }
}
