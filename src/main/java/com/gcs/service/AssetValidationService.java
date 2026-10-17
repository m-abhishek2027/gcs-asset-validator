
package com.gcs.service;

import com.google.cloud.storage.Blob;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AssetValidationService {

    public List<String> validate(Blob blob) {
        List<String> errors = new ArrayList<>();

        // Rule 1: Object name must not be empty
        if (blob.getName() == null || blob.getName().isBlank()) {
            errors.add("Object name is missing");
        }

        // Rule 2: File must not be empty
        if (blob.getSize() == null || blob.getSize() <= 0) {
            errors.add("File is empty or its size is unavailable");
        }

        // Rule 3: Content type must be available
        if (blob.getContentType() == null
                || blob.getContentType().isBlank()) {
            errors.add("Content type is missing");
        }

        return errors;
    }
}
