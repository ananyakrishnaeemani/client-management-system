package com.advisor.automation.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ExcelReader {
    private static final String DEFAULT_XLSX_RESOURCE = "TestData.xlsx";
    private static final String DOB_KEY = "DOB";

    public static Map<String, String> getTestData(String testCaseId) {
        if (testCaseId == null || testCaseId.isBlank()) {
            throw new IllegalArgumentException("testCaseId must not be null/blank");
        }

        Map<String, String> testData = new HashMap<>();

        DataFormatter formatter = new DataFormatter();
        try (InputStream is = ExcelReader.class.getClassLoader().getResourceAsStream(DEFAULT_XLSX_RESOURCE)) {
            if (is == null) {
                throw new IOException("Could not find '" + DEFAULT_XLSX_RESOURCE + "' on test classpath.");
            }

            try (Workbook workbook = new XSSFWorkbook(is)) {
                Sheet sheet = workbook.getSheetAt(0);
                Row headerRow = sheet.getRow(0);
                if (headerRow == null) {
                    throw new IllegalStateException(
                            "Excel header row (row 0) is missing in '" + DEFAULT_XLSX_RESOURCE + "'."
                    );
                }

                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row currentRow = sheet.getRow(i);
                    if (currentRow == null) continue;

                    Cell idCell = currentRow.getCell(0);
                    String currentId = (idCell == null) ? "" : formatter.formatCellValue(idCell).trim();
                    if (!testCaseId.equals(currentId)) continue;

                    for (int j = 0; j < headerRow.getLastCellNum(); j++) {
                        Cell headerCell = headerRow.getCell(j);
                        if (headerCell == null) continue;

                        String key = formatter.formatCellValue(headerCell).trim();
                        if (key.isBlank()) continue;

                        Cell cell = currentRow.getCell(j);
                        String value = (cell == null) ? "" : extractCellValue(key, cell, formatter);
                        testData.put(key, value);
                    }
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (testData.isEmpty()) {
            throw new IllegalArgumentException(
                    "No Excel row found for testCaseId='" + testCaseId + "' in '" + DEFAULT_XLSX_RESOURCE + "'."
            );
        }

        return testData;
    }

    private static String extractCellValue(String key, Cell cell, DataFormatter formatter) {
        if (key != null && DOB_KEY.equalsIgnoreCase(key.trim())) {
            return extractDob(cell, formatter);
        }
        return (cell == null) ? "" : formatter.formatCellValue(cell).trim();
    }

    /**
     * Angular date input expects strict "YYYY-MM-DD".
     * Excel can store DOB as a real date cell; DataFormatter may output a locale-specific
     * string, which breaks form validation. This method converts date cells to ISO.
     */
    private static String extractDob(Cell cell, DataFormatter formatter) {
        if (cell == null) return "";
        // Excel dates are often stored as a "serial number" (numeric cell), sometimes even when the cell
        // formatting isn't recognized as a date. We handle both cases:
        // 1) numeric cell -> treat as Excel serial day and convert
        // 2) string cell -> parse multiple common date formats

        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                double numeric = cell.getNumericCellValue();
                Date d = DateUtil.getJavaDate(numeric);
                if (d != null) {
                    LocalDate ld = Instant.ofEpochMilli(d.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
                    return ld.format(DateTimeFormatter.ISO_LOCAL_DATE);
                }
            }
        } catch (Exception ignored) {
            // fall through to string parsing
        }

        String raw = formatter.formatCellValue(cell).trim();
        if (raw.isBlank()) return "";

        // Normalize some common separators and unicode hyphen variants.
        String normalized = raw.replace('\u2212', '-').replace('/', '-').trim();

        // If the "raw" is a pure number (Excel serial exported as text), convert it to date.
        if (normalized.matches("^\\d{1,6}(\\.\\d+)?$")) {
            try {
                double serial = Double.parseDouble(normalized);
                Date d = DateUtil.getJavaDate(serial);
                if (d != null) {
                    LocalDate ld = Instant.ofEpochMilli(d.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
                    return ld.format(DateTimeFormatter.ISO_LOCAL_DATE);
                }
            } catch (Exception ignored) {
                // fall through
            }
        }

        // If already ISO-ish, accept.
        if (normalized.matches("\\d{4}-\\d{2}-\\d{2}")) return normalized;

        // Try a few common patterns from Excel exports.
        String[] patterns = new String[] {
                "dd-MM-uuuu", "dd-MM-uu", "dd-MM-yy",
                "dd-MMM-uuuu", "dd-MMM-uu", "dd-MMM-yy",
                "MM-dd-uuuu", "MM-dd-uu", "MM-dd-yy",
                "uuuu-MM-dd"
        };
        for (String p : patterns) {
            try {
                DateTimeFormatter df = DateTimeFormatter.ofPattern(p);
                LocalDate ld = LocalDate.parse(normalized, df);
                return ld.format(DateTimeFormatter.ISO_LOCAL_DATE);
            } catch (DateTimeParseException ignored) {
                // try next
            }
        }

        // As a last resort, return the raw value; submit() will fail and screenshot will show the issue.
        return raw;
    }
}
