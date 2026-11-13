package com.example.risk.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class ApiClient {

    private final RestClient restClient;

    // application.properties等からAPIキーを注入する想定の設計
    @Value("${gemini.api.key}")
    private String apiKey;

    public ApiClient(RestClient.Builder restClientBuilder) {
        // Gemini APIのベースURLと共通ヘッダーの設定
        this.restClient = restClientBuilder
                .baseUrl("https://generativelanguage.googleapis.com/v1beta")
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    /**
     * 画像データを含むリクエストオブジェクトを受け取り、Gemini APIへ送信して接続・応答取得のみを行う
     * 
     * @param requestBody Gemini APIの仕様に合わせたリクエストDTO（画像やプロンプトを含む）
     * @return Gemini APIからのレスポンス文字列（またはJSONオブジェクト）
     */
    public String sendImageToGemini(Object requestBody) {
        try {
            // HTTPS通信によるAPI呼び出し
            return restClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/models/gemini-1.5-flash:generateContent")
                            .queryParam("key", apiKey)
                            .build())
                    .body(requestBody)
                    .retrieve()
                    .body(String.class); // 通信結果のJSONをそのまま文字列として受け取る
                    
        } catch (RestClientException e) {
            // 通信エラーやタイムアウトなどのハンドリング
            throw new RuntimeException("Google Gemini APIとの通信に失敗しました。", e);
        }
    }
}