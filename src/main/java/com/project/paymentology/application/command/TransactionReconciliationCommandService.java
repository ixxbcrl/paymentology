package com.project.paymentology.application.command;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import com.project.paymentology.apis.common.ErrorCode;
import com.project.paymentology.apis.dtos.TransactionReconcileDto;
import com.project.paymentology.apis.dtos.TransactionReconcileResponseDto;
import com.project.paymentology.application.exception.BadRequestException;
import com.project.paymentology.application.exception.WebServiceException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TransactionReconciliationCommandService {
    public static final int TRANSACTION_COMPLETE_MATCH = 21;
    public static final int TRANSACTION_CLOSE_MATCH = 10;
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public List<TransactionReconcileResponseDto> reconcile(MultipartFile fileOne, MultipartFile fileTwo) {
        Map<String, List<TransactionReconcileDto>> parsedDtoMapFileOne;
        Map<String, List<TransactionReconcileDto>> parsedDtoMapFileTwo;
        List<TransactionReconcileResponseDto> resultDtos;

        parsedDtoMapFileOne = parseFileToMap(fileOne);
        parsedDtoMapFileTwo = parseFileToMap(fileTwo);
        resultDtos = reconcileParsedFiles(parsedDtoMapFileOne, parsedDtoMapFileTwo);

        System.out.println("parsedDtoMapFileOne size: " + parsedDtoMapFileOne.size());
        System.out.println("parsedDtoMapFileTwo size: " + parsedDtoMapFileTwo.size());

        for (TransactionReconcileResponseDto dto : resultDtos) {
            System.out.println("DTO source: " + dto.sourceFile + " -- " + "DTO unmatched: " + dto.unmatchedTransaction);
            if (dto.exactMatchTransaction != null)
                System.out.println("DTO exact: " + dto.exactMatchTransaction);
            if (dto.closeMatches != null)
                System.out.println("DTO close match size: " + dto.closeMatches.size());
        }
        return resultDtos;
    }

    private Map<String, List<TransactionReconcileDto>> parseFileToMap(MultipartFile file) {
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
                        .transactionDate(LocalDateTime.parse(cur.get(1), DATE_FORMATTER))
                        .transactionAmount(Double.parseDouble(cur.get(2)))
                        .transactionNarrative(cur.get(3))
                        .transactionDescription(cur.get(4))
                        .transactionID(cur.get(5))
                        .transactionType(Integer.parseInt(cur.get(6)))
                        .walletReference(cur.get(7))
                        .weightedCompare(0)
                        .build();
                parsedDtoMap.putIfAbsent(dto.getTransactionID(), new ArrayList<>());
                parsedDtoMap.get(dto.getTransactionID()).add(dto);
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
            log.error(String.format("An unexpected error occurred while parsing the files: %s", e.getMessage()), e);
            throw new BadRequestException(ErrorCode.FILE_OPERATION_FAILED, "An unexpected error occurred while parsing the files");
        }

        return parsedDtoMap;
    }

    private List<TransactionReconcileResponseDto> weightedCompareLists(List<TransactionReconcileDto> mapOneVal, List<TransactionReconcileDto> mapTwoVal) {
        List<TransactionReconcileResponseDto> responseDtos = new ArrayList<>();

        //return mapOne values if mapTwo is null. Means all mapOne values are unmatched.
        if (mapTwoVal == null) {
            return mapOneVal.stream()
                    .map(dto -> TransactionReconcileResponseDto.builder()
                            .unmatchedTransaction(dto)
                            .sourceFile("One")
                            .build())
                    .collect(Collectors.toList());
        }

        for (TransactionReconcileDto dtoOne : mapOneVal) {
            TransactionReconcileResponseDto responseDto = TransactionReconcileResponseDto.builder()
                    .unmatchedTransaction(dtoOne)
                    .closeMatches(new ArrayList<>())
                    .sourceFile("One")
                    .build();

            for (TransactionReconcileDto dtoTwo : mapTwoVal) {
                int weightedCompare = dtoOne.weightedCompare(dtoTwo);
                dtoTwo.weightedCompare = Math.max(dtoTwo.weightedCompare, weightedCompare);

                if (weightedCompare == TRANSACTION_COMPLETE_MATCH) {
                    responseDto.exactMatchTransaction = dtoTwo;
                } else if (weightedCompare >= TRANSACTION_CLOSE_MATCH) {
                    responseDto.closeMatches.add(dtoTwo);
                }
            }

            List<TransactionReconcileResponseDto> mapTwoUnmatched = mapTwoVal.stream()
                    .filter(it -> it.weightedCompare < TRANSACTION_CLOSE_MATCH)
                    .map(dto -> TransactionReconcileResponseDto.builder()
                            .unmatchedTransaction(dto)
                            .sourceFile("Two")
                            .build())
                    .collect(Collectors.toList());

            responseDtos.add(responseDto);
            responseDtos.addAll(mapTwoUnmatched);
        }

        return responseDtos;
    }

    private List<TransactionReconcileResponseDto> reconcileParsedFiles(Map<String, List<TransactionReconcileDto>> mapOne,
                                      Map<String, List<TransactionReconcileDto>> mapTwo) {
        List<TransactionReconcileResponseDto> responseDtos = new ArrayList<>();

        for (Map.Entry<String, List<TransactionReconcileDto>> mapOneEntry : mapOne.entrySet()) {
            String mapOneKey = mapOneEntry.getKey();
            List<TransactionReconcileDto> mapOneVal = mapOneEntry.getValue();

            responseDtos.addAll(weightedCompareLists(mapOneVal, mapTwo.get(mapOneKey)));
            mapTwo.remove(mapOneKey);
        }

        //Add all remaining orphans from mapTwo
        for (List<TransactionReconcileDto> remainingOrphans : mapTwo.values()) {
            responseDtos.addAll(remainingOrphans.stream()
                    .map(dto -> TransactionReconcileResponseDto.builder()
                            .unmatchedTransaction(dto)
                            .sourceFile("Two")
                            .build())
                    .collect(Collectors.toList()));
        }


        return responseDtos;
    }
}
