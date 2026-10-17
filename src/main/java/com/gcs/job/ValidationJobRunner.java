
package com.gcs.job;

import com.gcs.service.DuplicateDetectionService;
import com.gcs.service.DuplicateReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ValidationJobRunner implements CommandLineRunner {

    private static final Logger logger =
            LoggerFactory.getLogger(ValidationJobRunner.class);

    private final DuplicateDetectionService duplicateDetectionService;
    private final DuplicateReportService duplicateReportService;

    public ValidationJobRunner(
            DuplicateDetectionService duplicateDetectionService,
            DuplicateReportService duplicateReportService) {
        this.duplicateDetectionService = duplicateDetectionService;
        this.duplicateReportService = duplicateReportService;
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("Duplicate detection job started");

        var duplicates = duplicateDetectionService.findDuplicates();

        logger.info("Generating Excel report...");

        String reportPath =
                duplicateReportService.generateAndUploadReport(duplicates);

        logger.info("Excel report uploaded successfully: {}", reportPath);
        logger.info("Duplicate detection job completed");
    }
}
