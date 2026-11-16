package com.example.risk.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClient;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.net.InetSocketAddress;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.net.Proxy;
import java.net.InetSocketAddress;

@Component
public class GeminiApiClient {

    private final RestClient restClient;

    // プロキシを作成
    Proxy proxy = new Proxy(
        Proxy.Type.HTTP,
        new InetSocketAddress("172.17.0.2", 80)
    );

    // HTTP通信にプロキシを適用
        SimpleClientHttpRequestFactory factory =
        new SimpleClientHttpRequestFactory();

        factory.setProxy(proxy);

    @Value("${gemini.api.key}")
    private String apiKey;

    // モデル名
    private static final String MODEL_NAME = "gemini-3.8-flash";

    public GeminiApiClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .baseUrl("https://generativelanguage.googleapis.com/v1beta")
                .defaultHeader("Content-Type", "application/json")
                .requestFactory(factory)
                .build();
    }

    /**
     * 日本語のプロンプトと画像データをGemini APIへ送信する
     * 
     * @param prompt     日本語の指示や質問プロンプト
     * @param imageBytes 送信する画像のバイト配列（PNGやJPEGなど）
     * @param mimeType   画像のMIMEタイプ（例: "image/jpeg", "image/png"）
     * @return APIからのレスポンス（JSON文字列）
     */
    public String sendImageAndPromptToGemini(String prompt, byte[] imageBytes, String mimeType) {
        try {
            // 1. 画像をBase64エンコード
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            // 2. 型安全にMapを構築（Null安全警告やunchecked conversionを回避）
            Map<String, Object> inlineData = new HashMap<>();
            inlineData.put("mime_type", mimeType);
            inlineData.put("data", base64Image);

            Map<String, Object> inlineDataPart = new HashMap<>();
            inlineDataPart.put("inline_data", inlineData);

            Map<String, Object> textPart = new HashMap<>();
            textPart.put("text", prompt);

            Map<String, Object> content = new HashMap<>();
            content.put("parts", List.of(textPart, inlineDataPart));

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("contents", List.of(content));

            // 3. APIリクエストの送信
            return restClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/models/{model}:generateContent")
                            .queryParam("key", apiKey)
                            .build(MODEL_NAME))
                    .body(requestBody)
                    .retrieve()
                    .body(String.class);
                
        } catch (RestClientException e) {
            throw new RuntimeException("Google Gemini APIとの通信に失敗しました。", e);
        }
    }
}