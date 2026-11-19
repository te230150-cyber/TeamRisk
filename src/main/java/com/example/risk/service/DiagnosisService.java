package com.example.risk.service;

import com.example.risk.client.GeminiApiClient;
import com.example.risk.model.DiagnosisForm;
import com.example.risk.model.DiagnosisResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class DiagnosisService {

    private static final Logger log = LoggerFactory.getLogger(DiagnosisService.class);
    private static final long TIMEOUT_SECONDS = 120L; // 設計書 7.3.1: 120秒タイムアウト

    // プロンプトv2.0.txt の指示文[cite: 9]
    private static final String LOCATION_ESTIMATION_PROMPT = """
            写真が撮影された場所を推定し、その根拠を説明してください。

            画像から直接確認できる情報と、AIの知識による推測を区別してください。

            推定した場所について、画像内の具体的な特徴と一致する根拠を示してください。画像から確認できない施設名や周辺環境を、確認済みの事実として扱わないでください。

            根拠が不十分な場合は「推定困難」と回答してください。
            """;

    private final GeminiApiClient geminiApiClient;
    private final RiskScoringService riskScoringService;

    public DiagnosisService(GeminiApiClient geminiApiClient, RiskScoringService riskScoringService) {
        this.geminiApiClient = geminiApiClient;
        this.riskScoringService = riskScoringService;
    }

    /**
     * 画像診断を実行する（通常診断／学習用画像診断共通）
     */
    public DiagnosisResult executeDiagnosis(DiagnosisForm form, byte[] resolvedImageBytes, String mimeType) {
        log.info("画像診断処理を開始します。mode={}", form.getMode()); // 設計書 7.3.3 処理ログ

        // 1. 入力チェック（設計書 5.3・7.3.2）
        validateInput(form, resolvedImageBytes, mimeType);

        // メモリ上の一時保持データ（終了時に必ず破棄）
        byte[] tempImageBuffer = resolvedImageBytes;
        String tempAnalysisRaw = null;

        try {
            // 2. 120秒タイムアウト制御付きで Gemini API 呼び出し（画像解析・撮影場所推定）
            byte[] imageToSend = tempImageBuffer;
            CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
                String base64Image = Base64.getEncoder().encodeToString(imageToSend);
                Object payload = buildGeminiPayload(base64Image, mimeType, LOCATION_ESTIMATION_PROMPT);
                return geminiApiClient.sendImageToGemini(payload);
            });

            tempAnalysisRaw = future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);

            // 3. 解析・推定結果の整形と危険度判定（設計書 6.1〜6.3, 6.6, 6.7）
            return buildDiagnosisResult(tempAnalysisRaw);

        } catch (TimeoutException e) {
            log.error("診断処理がタイムアウト（{}秒）しました。", TIMEOUT_SECONDS);
            throw new IllegalStateException("診断処理がタイムアウトしました。もう一度お試しください。", e); // 設計書 7.3.2[cite: 8]
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("診断処理が中断されました。", e);
            throw new IllegalStateException("画像の解析に失敗しました。", e);
        } catch (ExecutionException e) {
            log.error("外部API連携または画像解析中にエラーが発生しました。", e.getCause());
            throw new IllegalStateException("画像の解析に失敗しました。", e.getCause()); // 設計書 7.3.2[cite: 8]
        } finally {
            // 4. 正常終了・異常終了・タイムアウトに関わらず一時データを削除[cite: 8]
            tempImageBuffer = null;
            tempAnalysisRaw = null;
            log.info("画像診断処理を終了し、一時データを破棄しました。");
        }
    }

    private void validateInput(DiagnosisForm form, byte[] imageBytes, String mimeType) {
        if (imageBytes == null || imageBytes.length == 0) {
            throw new IllegalArgumentException("診断対象の画像を選択してください。");
        }
        if (!"image/jpeg".equals(mimeType) && !"image/png".equals(mimeType)) {
            throw new IllegalArgumentException("JPEGまたはPNG形式の画像を選択してください。"); // 設計書 7.3.2[cite: 8]
        }
        long maxBytes = Duration.ofMegabytes(10).toBytes();
        if (imageBytes.length > maxBytes) {
            throw new IllegalArgumentException("10MB以下の画像を選択してください。"); // 設計書 7.3.2[cite: 8]
        }
    }

    private Object buildGeminiPayload(String base64Data, String mimeType, String prompt) {
        // Gemini REST API (generateContent) 向けのペイロード構造[cite: 8, 9]
        return java.util.Map.of(
                "contents", List.of(
                        java.util.Map.of(
                                "parts", List.of(
                                        java.util.Map.of("text", prompt),
                                        java.util.Map.of(
                                                "inline_data", java.util.Map.of(
                                                        "mime_type", mimeType,
                                                        "data", base64Data
                                                )
                                        )
                                )
                        )
                )
        );
    }

    private DiagnosisResult buildDiagnosisResult(String rawResponse) {
        // TODO: Geminiレスポンスから「判定結果・推定場所・推定根拠・検出要因」を抽出するパーサーと連携[cite: 8]
        List<String> detectedFactors = List.of();
        boolean prefectureEstimated = false;
        boolean municipalityEstimated = false;

        RiskScoringService.ScoringOutcome outcome = riskScoringService.calculate(
                detectedFactors, prefectureEstimated, municipalityEstimated
        );

        DiagnosisResult result = new DiagnosisResult();
        result.setEstimationStatus(municipalityEstimated ? "判定可能" : "推定困難"); // 設計書 6.2[cite: 8]
        result.setEstimatedLocation(municipalityEstimated ? "推定された市区町村" : "推定困難");
        result.setEstimationBasis(rawResponse != null ? rawResponse : "根拠不足のため推定困難");
        result.setDetectedFactors(outcome.factors());
        result.setTotalScore(outcome.totalScore());
        result.setRiskLevel(outcome.riskLevel());
        result.setJudgmentReason(outcome.reason());
        return result;
    }
}