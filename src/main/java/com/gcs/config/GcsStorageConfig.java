package com.gcs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

@Configuration
public class GcsStorageConfig {

	@Bean
	public Storage storage() {
		return StorageOptions.getDefaultInstance().getService();
	}
}
