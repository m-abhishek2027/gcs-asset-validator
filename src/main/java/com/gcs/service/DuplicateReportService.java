
package com.gcs.service;

import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class DuplicateReportService {

    private final Storage storage;

    @Value("${gcs.bucket-name}")
    private String bucketName;

    @Value("${gcs.report-folder}")
    private String reportFolder;

    public DuplicateReportService(Storage storage) {
        this.storage = storage;
    }

    public String generateAndUploadReport(
            Map<String, List<String>> duplicates) throws Exception {

        String timestamp = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));

        String fileName = "duplicate-report-" + timestamp + ".xlsx";
        String objectName = reportFolder + fileName;

        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Duplicate Report");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Duplicate Group");
            header.createCell(1).setCellValue("MD5 and Size");
            header.createCell(2).setCellValue("Object Name");

            int rowNumber = 1;
            int groupNumber = 1;

            for (Map.Entry<String, List<String>> entry
                    : duplicates.entrySet()) {

                for (String objectNameInBucket : entry.getValue()) {
                    Row row = sheet.createRow(rowNumber++);

                    row.createCell(0).setCellValue(groupNumber);
                    row.createCell(1).setCellValue(entry.getKey());
                    row.createCell(2).setCellValue(objectNameInBucket);
                }

                groupNumber++;
            }

            for (int i = 0; i < 3; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(output);

            BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, objectName)
                    .setContentType(
                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    .build();

            storage.create(blobInfo, output.toByteArray());
        }

        return "gs://" + bucketName + "/" + objectName;
    }
}
