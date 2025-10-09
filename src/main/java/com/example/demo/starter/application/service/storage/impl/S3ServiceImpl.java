package com.example.demo.starter.application.service.storage.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.example.demo.starter.application.service.storage.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.nio.file.Path;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3ServiceImpl implements S3Service {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    /**
     * Uploads a file to S3 and returns its public URL.
     */
    public String upload(Path filePath, String key) {
        try {
            File file = filePath.toFile();
            PutObjectRequest request = new PutObjectRequest(bucketName, key, file)
                    .withCannedAcl(CannedAccessControlList.PublicRead); // make this object publicly readable

            amazonS3.putObject(request);
            String url = amazonS3.getUrl(bucketName, key).toString();
            log.info("File uploaded successfully to S3: {}", url);
            return url;
        } catch (Exception e) {
            log.error("Failed to upload file to S3: {}", e.getMessage(), e);
            throw new RuntimeException("S3 upload failed", e);
        }
    }

    /**
     * Deletes a file from S3 by its key.
     */
    public void delete(String key) {
        try {
            amazonS3.deleteObject(new DeleteObjectRequest(bucketName, key));
            log.info("File deleted from S3: {}", key);
        } catch (Exception e) {
            log.error("Failed to delete file from S3: {}", e.getMessage(), e);
            throw new RuntimeException("S3 delete failed", e);
        }
    }

    /**
     * Checks if a file exists in the S3 bucket.
     */
    public boolean exists(String key) {
        try {
            return amazonS3.doesObjectExist(bucketName, key);
        } catch (Exception e) {
            log.error("Failed to check S3 object existence: {}", e.getMessage(), e);
            return false;
        }
    }
}
