package com.project.paymentology.application.command;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import com.project.paymentology.apis.common.ErrorCode;
import com.project.paymentology.apis.common.ErrorResult;
import com.project.paymentology.apis.dtos.TransactionReconcileDto;
import com.project.paymentology.apis.dtos.TransactionReconcileResponseDto;
import com.project.paymentology.apis.dtos.TransactionReconcileSummaryDto;
import com.project.paymentology.application.exception.BadRequestException;
import com.project.paymentology.application.exception.WebServiceException;
import com.project.paymentology.application.utils.CsvFieldValidator;
import org.springframework.stereotype.Service;
import org.springframework.validation.DataBinder;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.project.paymentology.apis.common.ErrorCode.DATA_FIELD_VALIDATION_EXCEPTION;

@Slf4j
@Service
public class TransactionReconciliationCommandService {
    public static final int TRANSACTION_COMPLETE_MATCH = 21;
    public static final int TRANSACTION_CLOSE_MATCH = 10;
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    public TransactionReconcileSummaryDto reconcile(MultipartFile fileOne, MultipartFile fileTwo) {
        if (fileOne.isEmpty() || fileTwo.isEmpty()) {
            throw new BadRequestException(ErrorCode.INVALID_FILES, "Error parsing files. Please make sure there are valid files attached to the request.");
        }
        TransactionReconcileSummaryDto summaryDto = TransactionReconcileSummaryDto.builder()
                .fileOneTotalRecords(0)
                .fileTwoTotalRecords(0)
                .fileOneUnmatchedRecords(0)
                .fileTwoUnmatchedRecords(0)
                .matchingRecords(0)
                .transactionReconcileResponseDto(new ArrayList<>())
                .build();

        Map<String, List<TransactionReconcileDto>> parsedDtoMapFileOne;
        Map<String, List<TransactionReconcileDto>> parsedDtoMapFileTwo;

        parsedDtoMapFileOne = parseFileToMap(fileOne, "One", summaryDto);
        parsedDtoMapFileTwo = parseFileToMap(fileTwo, "Two", summaryDto);
        reconcileParsedFiles(parsedDtoMapFileOne, parsedDtoMapFileTwo, summaryDto);

        return summaryDto;
    }

    private Map<String, List<TransactionReconcileDto>> parseFileToMap(MultipartFile file, String fileNumber,
                                                                      TransactionReconcileSummaryDto summaryDto) {
        Map<String, List<TransactionReconcileDto>> parsedDtoMap = new HashMap<>();
        List<Long> errorLines = new ArrayList<>();
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
                List<String> row = Arrays.asList(values);

                try {
                    TransactionReconcileDto dto = TransactionReconcileDto.builder()
                            .transactionDate(LocalDateTime.parse(row.get(1), DATE_FORMATTER))
                            .transactionAmount(Long.parseLong(row.get(2)))
                            .transactionNarrative(row.get(3))
                            .transactionDescription(row.get(4))
                            .transactionID(row.get(5))
                            .transactionType(Integer.parseInt(row.get(6)))
                            .walletReference(row.get(7))
                            .lineNumber(csvReader.getLinesRead())
                            .weightedCompare(0)
                            .build();
                    parsedDtoMap.putIfAbsent(dto.getTransactionID(), new ArrayList<>());
                    parsedDtoMap.get(dto.getTransactionID()).add(dto);
                } catch (Exception e) {
                    errorLines.add(csvReader.getLinesRead());
                }
            }

            if (fileNumber.equals("One")) {
                summaryDto.setFileOneTotalRecords(csvReader.getLinesRead()-1);
            } else {
                summaryDto.setFileTwoTotalRecords(csvReader.getLinesRead()-1);
            }
        } catch (IOException | CsvValidationException e) {
            log.error(String.format("An unexpected error occurred while parsing the files: %s", e.getMessage()), e);
            throw new BadRequestException(ErrorCode.FILE_OPERATION_FAILED, "An unexpected error occurred while parsing the files");
        }

        if (!errorLines.isEmpty()) {
            throw new BadRequestException(DATA_FIELD_VALIDATION_EXCEPTION, String.format("Validations failed for %s at lines: %s",
                    file.getOriginalFilename(), errorLines.stream()
                            .map(String::valueOf)
                            .collect(Collectors.joining(","))));
        }

        return parsedDtoMap;
    }

    /**
     *  Calculates and assigns the weightedCompare value to dtos.
     *  weightedCompare of < TRANSACTION_CLOSE_MATCH = no match.
     *  weightedCompare of == TRANSACTION_COMPLETE_MATCH = exact match.
     *  weightedCompare of >= TRANSACTION_CLOSE_MATCH = close match.
     */
    private void weightedCompareLists(List<TransactionReconcileDto> mapOneVal,
                                      List<TransactionReconcileDto> mapTwoVal,
                                      TransactionReconcileSummaryDto summaryDto) {
        List<TransactionReconcileResponseDto> responseDtos = new ArrayList<>();

        if (mapTwoVal == null) {
            summaryDto.incrementFileOneUnmatchedRecords();
            responseDtos = mapOneVal.stream()
                    .map(dto -> TransactionReconcileResponseDto.builder()
                            .unmatchedTransaction(dto)
                            .sourceFile("One")
                            .build()).toList();
            summaryDto.getTransactionReconcileResponseDto().addAll(responseDtos);
        } else {
            for (TransactionReconcileDto dtoOne : mapOneVal) {
                TransactionReconcileResponseDto responseDto = TransactionReconcileResponseDto.builder()
                        .unmatchedTransaction(dtoOne)
                        .closeMatches(new ArrayList<>())
                        .sourceFile("One")
                        .build();

                for (TransactionReconcileDto dtoTwo : mapTwoVal) {
                    int weightedCompare = dtoOne.weightedCompare(dtoTwo);

                    if (weightedCompare == TRANSACTION_COMPLETE_MATCH && dtoTwo.weightedCompare != TRANSACTION_COMPLETE_MATCH) {
                        responseDto.exactMatchTransaction = dtoTwo;
                        summaryDto.incrementMatchingRecords();
                        dtoTwo.weightedCompare = Math.max(dtoTwo.weightedCompare, weightedCompare);
                        break;
                    } else if (weightedCompare >= TRANSACTION_CLOSE_MATCH && dtoTwo.weightedCompare != TRANSACTION_COMPLETE_MATCH) {
                        responseDto.closeMatches.add(dtoTwo);
                        dtoTwo.weightedCompare = Math.max(dtoTwo.weightedCompare, weightedCompare);
                    }
                }

                if (responseDto.exactMatchTransaction == null) {
                    if (responseDto.closeMatches.isEmpty()) {
                        summaryDto.incrementFileOneUnmatchedRecords();
                    }
                    responseDtos.add(responseDto);
                }
            }

            List<TransactionReconcileResponseDto> mapTwoUnmatched = mapTwoVal.stream()
                    .filter(it -> it.weightedCompare < TRANSACTION_CLOSE_MATCH)
                    .map(dto -> TransactionReconcileResponseDto.builder()
                            .unmatchedTransaction(dto)
                            .sourceFile("Two")
                            .build()).toList();
            summaryDto.setFileTwoUnmatchedRecords(summaryDto.getFileTwoUnmatchedRecords() + mapTwoUnmatched.size());
            responseDtos.addAll(mapTwoUnmatched);

            summaryDto.getTransactionReconcileResponseDto().addAll(responseDtos);
        }
    }

    private void reconcileParsedFiles(Map<String, List<TransactionReconcileDto>> mapOne,
                                                                Map<String, List<TransactionReconcileDto>> mapTwo,
                                                                TransactionReconcileSummaryDto summaryDto) {
        for (Map.Entry<String, List<TransactionReconcileDto>> mapOneEntry : mapOne.entrySet()) {
            String mapOneKey = mapOneEntry.getKey();
            List<TransactionReconcileDto> mapOneVal = mapOneEntry.getValue();

            weightedCompareLists(mapOneVal, mapTwo.get(mapOneKey), summaryDto);
            mapTwo.remove(mapOneKey);
        }

        //Add all remaining orphans from mapTwo
        for (List<TransactionReconcileDto> remainingOrphans : mapTwo.values()) {
            summaryDto.getTransactionReconcileResponseDto().addAll(remainingOrphans.stream()
                    .map(dto -> TransactionReconcileResponseDto.builder()
                            .unmatchedTransaction(dto)
                            .sourceFile("Two")
                            .build()).toList());
            summaryDto.setFileTwoUnmatchedRecords(summaryDto.getFileTwoUnmatchedRecords() + remainingOrphans.size());
        }
    }
}
