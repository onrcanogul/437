package com.example.demo.starter.application.service.audio.impl;

import com.example.demo.starter.application.service.storage.StorageService;
import com.example.demo.starter.application.service.whisper.WhisperService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AudioServiceImpl {

    private final StorageService storageService;
    private final WhisperService whisperService;

    /**
     * Handles the full process:
     * Save MP4 file temporarily
     * Convert it to MP3 using FFmpeg
     * Upload MP3 to cloud storage
     * Send it to Whisper for transcription
     * Delete both local and cloud files
     */
    public String processAudioAndTranscribe(MultipartFile mp4File)
            throws IOException, InterruptedException {
        Path tempMp4 = Files.createTempFile("meeting_", ".mp4");
        try (InputStream in = mp4File.getInputStream()) {
            Files.copy(in, tempMp4, StandardCopyOption.REPLACE_EXISTING);
        }
        log.info("Temporary MP4 file created: {}", tempMp4);

        Path mp3Path = convertMp4ToMp3(tempMp4);

        Map<String, String> cloudInfo = uploadToStorage(mp3Path);
        String transcript = extractTranscript(cloudInfo.get("url"));
        removeTemporaryFiles(cloudInfo.get("key"), tempMp4, mp3Path);

        return transcript;
    }

    private Map<String, String> uploadToStorage(Path mp3Path) {
        String s3Key = "audio/" + mp3Path.getFileName();
        String s3Url = storageService.upload(mp3Path, s3Key);
        log.info("File uploaded to cloud: {}", s3Url);
        return Map.of("key", s3Key, "url", s3Url);
    }

    private String extractTranscript(String storageUrl) {
        String transcript = whisperService.transcribeFromUrl(storageUrl);
        log.info("Transcript retrieved, length: {}", transcript.length());
        return transcript;
    }

    private void removeTemporaryFiles(String s3Key, Path tempMp4, Path mp3Path) throws IOException {
        storageService.delete(s3Key);
        Files.deleteIfExists(tempMp4);
        Files.deleteIfExists(mp3Path);
        log.info("Temporary and cloud files deleted.");
    }

    private Path convertMp4ToMp3(Path mp4Path) throws IOException, InterruptedException {
        Path mp3Path = Path.of(mp4Path.toString().replace(".mp4", ".mp3"));

        ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg",
                "-i", mp4Path.toString(),
                "-vn",
                "-acodec", "libmp3lame",
                mp3Path.toString()
        );
        pb.redirectErrorStream(true);
        Process process = pb.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            reader.lines().forEach(log::debug);
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("FFmpeg conversion failed with exit code: " + exitCode);
        }

        log.info("MP3 conversion completed: {}", mp3Path);
        return mp3Path;
    }
}


