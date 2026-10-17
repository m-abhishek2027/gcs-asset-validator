package com.gcs.service;

import org.springframework.stereotype.Service;
import com.google.cloud.storage.Storage;
import org.springframework.beans.factory.annotation.Value;




@Service
public class GcsStorageService {
	
	private final Storage storage;
	
	@Value("${gcs.bucket-name}")
	private String bucketName;
	
    public GcsStorageService(Storage storage) {
        this.storage = storage;
    }
    
 

}
