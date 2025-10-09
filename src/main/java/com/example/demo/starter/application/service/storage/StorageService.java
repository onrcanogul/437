package com.example.demo.starter.application.service.storage;

import java.nio.file.Path;

public interface StorageService {
    String upload(Path filePath, String key);
}
