package com.example.risk.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class GeminiApiClient {

    private static final Logger log = LoggerFactory.getLogger(GeminiApiClient.class);

    public String sendImageToGemini(Object payload) {
        log.debug("ダミー Gemini API を呼び出します。payloadType={}",
                payload != null ? payload.getClass().getSimpleName() : "null");

        // 動作確認用のダミー応答
        return """
                推定困難
                直接確認できる情報: 建物外観と道路標識の一部が写っているが、市区町村を特定できる固有名詞は判別できない。
                推測: 都市部の幹線道路沿いの可能性があるが、根拠が不十分。
                """;
    }
}