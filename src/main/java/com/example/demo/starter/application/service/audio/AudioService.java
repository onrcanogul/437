package com.example.demo.starter.application.service.audio;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface AudioService {
    String processAudioAndTranscribe(MultipartFile mp4File) throws IOException, InterruptedException;
}
