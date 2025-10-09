package com.example.demo.starter.application.service.storage.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.example.demo.starter.application.service.storage.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {
    private final AmazonS3 amazonS3;
    @Value("${aws.s3.bucket}")
    private String bucket;

    public String upload(Path filePath, String key) {
        amazonS3.putObject(bucket, key, filePath.toFile());
        return amazonS3.getUrl(bucket, key).toString();
    }
}
