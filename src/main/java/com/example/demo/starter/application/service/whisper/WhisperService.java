package com.example.demo.starter.application.service.whisper;

public interface WhisperService {
    String transcribeFromUrl(String s3Url);
}
