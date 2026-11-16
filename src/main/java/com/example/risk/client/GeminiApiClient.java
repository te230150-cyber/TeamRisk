package com.example.risk.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class GeminiApiClient {

    private final RestClient restClient;

    @Value("${gemini.api.key}")
    private String apiKey;

    public GeminiApiClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .baseUrl("https://generativelanguage.googleapis.com/v1beta")
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    /**
     * Gemini APIへリクエストを送信し、通信と応答取得のみを行う
     * 
     * @param requestBody Gemini APIへの送信データ（オブジェクト）
     * @return APIからのレスポンス（JSON文字列）
     */
    public String sendImageToGemini(Object requestBody) {
        try {
            return restClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/models/gemini-1.5-flash:generateContent")
                            .queryParam("key", apiKey)
                            .build())
                    .body(requestBody)
                    .retrieve()
                    .body(String.class);
                    
        } catch (RestClientException e) {
            throw new RuntimeException("Google Gemini APIとの通信に失敗しました。", e);
        }
    }
}