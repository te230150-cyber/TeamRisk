package com.example.risk.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GeminiApiClient {

    private final RestClient restClient;

    @Value("${gemini.api.key}")
    private String apiKey;

    // 無料版で利用できる最新のFlashモデル
    private static final String MODEL_NAME = "gemini-2.5-flash";

    // application.properties からプロキシ設定を読み込む（プロキシ不要な環境では空欄等で調整可能）
    public GeminiApiClient(
            RestClient.Builder restClientBuilder,
            @Value("${app.proxy.host:}") String proxyHost,
            @Value("${app.proxy.port:0}") int proxyPort) {

        // プロキシが設定されている場合は ClientHttpRequestFactory に適用する
        if (proxyHost != null && !proxyHost.isBlank() && proxyPort > 0) {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
            SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
            requestFactory.setProxy(proxy);
            restClientBuilder.requestFactory(requestFactory);
        }

        this.restClient = restClientBuilder
                .baseUrl("https://generativelanguage.googleapis.com/v1beta")
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    /**
     * 日本語のプロンプトと画像データをGemini APIへ送信する
     */
    public String sendImageAndPromptToGemini(String prompt, byte[] imageBytes, String mimeType) {
        try {
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            Map<String, Object> inlineData = new HashMap<>();
            inlineData.put("mime_type", mimeType);
            inlineData.put("data", base64Image);

            Map<String, Object> inlineDataPart = new HashMap<>();
            inlineDataPart.put("inline_data", inlineData);

            Map<String, Object> textPart = new HashMap<>();
            textPart.put("text", prompt);

            Map<String, Object> content = new HashMap<>();
            content.put("parts", List.of(textPart, inlineDataPart));

            Map<String, Object> thinkingConfig = new HashMap<>();
            thinkingConfig.put("thinkingLevel", "xhigh");

            Map<String, Object> generationConfig = new HashMap<>();
            generationConfig.put("thinkingConfig", thinkingConfig);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("contents", List.of(content));
            requestBody.put("generationConfig", generationConfig);

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