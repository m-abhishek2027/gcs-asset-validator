
package com.gcs.service;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class DuplicateDetectionService {

    private static final Logger logger =
            LoggerFactory.getLogger(DuplicateDetectionService.class);

    private final Storage storage;

    @Value("${gcs.bucket-name}")
    private String bucketName;
    
    @Value("${gcs.folder-prefix:}")
    private String folderPrefix;

    public DuplicateDetectionService(Storage storage) {
        this.storage = storage;
    }

    public Map<String, List<String>> findDuplicates() {

        Map<String, List<String>> objectsByMd5 = new LinkedHashMap<>();
        
        Storage.BlobListOption option =
                Storage.BlobListOption.prefix(folderPrefix);

        Page<Blob> blobs = storage.list(bucketName, option);

        long processed = 0;
        long missingMd5 = 0;

        for (Blob blob : blobs.iterateAll()) {

            // Skip folder-marker objects
            if (blob.getName().endsWith("/")) {
                continue;
            }

            processed++;

            String md5 = blob.getMd5ToHexString();

            // MD5 is not available for every GCS object
            if (md5 == null || md5.isBlank()) {
                missingMd5++;
                continue;
            }

            String key = md5 + ":" + blob.getSize();

            objectsByMd5
                    .computeIfAbsent(key, k -> new ArrayList<>())
                    .add(blob.getName());
        }

        Map<String, List<String>> duplicates = new LinkedHashMap<>();

        objectsByMd5.forEach((key, names) -> {
            if (names.size() > 1) {
                duplicates.put(key, names);
            }
        });

        logger.info("Objects processed: {}", processed);
        logger.info("Objects without MD5: {}", missingMd5);
        logger.info("Duplicate groups found: {}", duplicates.size());

        duplicates.forEach((key, names) ->
                logger.info("MD5 and size: {}, objects: {}", key, names));

        return duplicates;
    }
}
