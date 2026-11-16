package com.example.risk.controller;

import com.example.risk.client.GeminiApiClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/test/gemini")
public class GeminiTestController {

    private final GeminiApiClient geminiApiClient;

    public GeminiTestController(GeminiApiClient geminiApiClient) {
        this.geminiApiClient = geminiApiClient;
    }

    /**
     * 画像とプロンプトを受け取り、Gemini APIの応答をそのまま返す
     */
    @PostMapping("/send")
    public String testGemini(
            @RequestParam("prompt") String prompt,
            @RequestParam("image") MultipartFile image) throws IOException {
        
        byte[] imageBytes = image.getBytes();
        String mimeType = image.getContentType() != null ? image.getContentType() : "image/jpeg";

        // Gemini APIからのレスポンス（JSON文字列）をそのまま返す
        return geminiApiClient.sendImageAndPromptToGemini(prompt, imageBytes, mimeType);
    }
}