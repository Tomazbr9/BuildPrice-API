package com.tomazbr9.buildprice.batch;

import com.github.pjfanning.xlsx.StreamingReader;
import com.tomazbr9.buildprice.dto.sinapi.SinapiItemDTO;
import org.apache.poi.ss.usermodel.*;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;

@Component
@StepScope
public class SinapiItemReader implements ItemReader<SinapiItemDTO>, ItemStream {

    private Iterator<Row> rowIterator;
    private boolean headerFound = false;
    private Workbook workbook;
    private Map<Integer, String> ufColumns = new LinkedHashMap<>();


    @Value("#{jobParameters['tempFile']}") private String filePath;
    @Value("#{jobParameters['sheetName']}") private String sheetName;

    @Override
    public SinapiItemDTO read() throws Exception {

        if (rowIterator == null) {
            init();
        }

        while (rowIterator.hasNext()) {

            Row row = rowIterator.next();

            // ainda não encontrou o cabeçalho real
            if (!headerFound) {
                if (isHeaderRow(row)) {
                    headerFound = true; // próxima linha já é dado

                    parseHeader(row);

                }
                continue;
            }

            return mapRowToDTO(row);
        }

        close();
        return null;
    }


    private void init() throws IOException {

        InputStream is = new FileInputStream(filePath);
        workbook = StreamingReader.builder()
                .rowCacheSize(100)
                .bufferSize(4096)
                .open(is);

        Sheet sheet = workbook.getSheet(sheetName);

        if(sheet == null) throw new RuntimeException("Sheet não encontrado");

        rowIterator = sheet.iterator();

    }

    private void parseHeader(Row row){

        for(int i = 0; i < row.getLastCellNum(); i++){
            String value = parseString(row.getCell(i));
            if(value != null && value.matches("[A-Z]{2}")){
                ufColumns.put(i, value);
            }
        }
    }

    private boolean isHeaderRow(Row row) {

        String col0 = parseString(row.getCell(0));
        return col0 != null && col0.contains("Classificação");
    }


    private SinapiItemDTO mapRowToDTO(Row row) {

        Map<String, BigDecimal> prices = new HashMap<>();

        ufColumns.forEach((index, uf) -> {
            Cell cell = row.getCell(index);

            if(cell != null){
                prices.put(uf, parseBigDecimal(cell));
            }

        });

        return new SinapiItemDTO(
                parseString(row.getCell(0)),
                parseString(row.getCell(1)),
                parseString(row.getCell(2)),
                parseString(row.getCell(3)),
                sheetName,
                prices
        );

    }

    private String parseString(Cell cell) {
        if(cell == null) return null;
        DataFormatter formatter = new DataFormatter();
        return formatter.formatCellValue(cell).trim();
    }

    private BigDecimal parseBigDecimal(Cell cell) {
        if (cell == null) return null;

        DataFormatter formatter = new DataFormatter();
        String raw = formatter.formatCellValue(cell).trim();

        if (raw.isEmpty()) return null;

        // remove espaços e símbolos comuns
        String value = raw
                .replace("R$", "")
                .replace("%", "")
                .replace(" ", "");

        try {
            // Caso brasileiro: 1.234,56
            if (value.contains(".") && value.contains(",")) {
                value = value.replace(".", "").replace(",", ".");
            }
            // Caso inválido: 2.745.37 (dois pontos)
            else if (countOccurrences(value, '.') > 1) {
                throw new NumberFormatException("Formato inválido (múltiplos '.')");
            }
            // Caso brasileiro simples: 123,45
            else if (value.contains(",")) {
                value = value.replace(",", ".");
            }

            return new BigDecimal(value);

        } catch (NumberFormatException e) {
            System.out.println(
                    "⚠Valor numérico inválido ignorado: '" + raw + "'"
            );
            return null;
        }
    }

    private int countOccurrences(String value, char c) {
        int count = 0;
        for (char ch : value.toCharArray()) {
            if (ch == c) count++;
        }
        return count;
    }


    @Override
    public void close() throws ItemStreamException {
        if (workbook != null) {
            try {
                workbook.close();
            } catch (IOException e) {
                throw new ItemStreamException(e);
            }
        }
    }


}
