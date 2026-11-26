package com.example.risk;

import com.example.risk.service.RiskScoringService;
import com.example.risk.service.DiagnosisService;
import com.example.risk.client.GeminiApiClient;
import com.example.risk.model.DiagnosisForm;
import com.example.risk.model.DiagnosisResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DiagnosisServiceTest {

    private StubGeminiApiClient stubGeminiApiClient;
    private RiskScoringService riskScoringService;
    private DiagnosisService diagnosisService;

    @BeforeEach
    void setUp() {
        stubGeminiApiClient = new StubGeminiApiClient();
        riskScoringService = new RiskScoringService();
        diagnosisService = new DiagnosisService(stubGeminiApiClient, riskScoringService);
    }

    @Test
    @DisplayName("正常系：JPEG画像で診断を実行し結果が返却される")
    void executeDiagnosis_jpegSuccess() {
        DiagnosisForm form = new DiagnosisForm("NORMAL");
        byte[] jpegBytes = "dummy-jpeg-bytes".getBytes(StandardCharsets.UTF_8);

        DiagnosisResult result = diagnosisService.executeDiagnosis(form, jpegBytes, "image/jpeg");

        assertNotNull(result);
        assertEquals("推定困難", result.getEstimationStatus());
        assertEquals("推定困難", result.getEstimatedLocation());
        assertEquals("C判定", result.getRiskLevel());
        assertEquals(0, result.getTotalScore());
        assertEquals(List.of(), result.getDetectedFactors());
        assertNotNull(result.getEstimationBasis());
    }

    @Test
    @DisplayName("正常系：PNG画像で診断を実行できる")
    void executeDiagnosis_pngSuccess() {
        DiagnosisForm form = new DiagnosisForm("LEARNING");
        byte[] pngBytes = "dummy-png-bytes".getBytes(StandardCharsets.UTF_8);

        DiagnosisResult result = diagnosisService.executeDiagnosis(form, pngBytes, "image/png");

        assertNotNull(result);
        assertEquals("推定困難", result.getEstimationStatus());
        assertEquals("C判定", result.getRiskLevel());
    }

    @Test
    @DisplayName("異常系：画像バイト配列が空の場合はIllegalArgumentException")
    void executeDiagnosis_emptyBytes_throwsIllegalArgumentException() {
        DiagnosisForm form = new DiagnosisForm("NORMAL");

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> diagnosisService.executeDiagnosis(form, new byte[0], "image/jpeg")
        );

        assertEquals("診断対象の画像を選択してください。", ex.getMessage());
    }

    @Test
    @DisplayName("異常系：非対応のMIMEタイプの場合はIllegalArgumentException")
    void executeDiagnosis_unsupportedMimeType_throwsIllegalArgumentException() {
        DiagnosisForm form = new DiagnosisForm("NORMAL");
        byte[] bytes = "dummy-bytes".getBytes(StandardCharsets.UTF_8);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> diagnosisService.executeDiagnosis(form, bytes, "image/gif")
        );

        assertEquals("JPEGまたはPNG形式の画像を選択してください。", ex.getMessage());
    }

    @Test
    @DisplayName("異常系：10MBを超える画像の場合はIllegalArgumentException")
    void executeDiagnosis_overMaxSize_throwsIllegalArgumentException() {
        DiagnosisForm form = new DiagnosisForm("NORMAL");
        byte[] overSizeBytes = new byte[10 * 1024 * 1024 + 1];

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> diagnosisService.executeDiagnosis(form, overSizeBytes, "image/jpeg")
        );

        assertEquals("10MB以下の画像を選択してください。", ex.getMessage());
    }

    @Test
    @DisplayName("異常系：Gemini API呼び出しで例外が発生した場合はIllegalStateException")
    void executeDiagnosis_apiError_throwsIllegalStateException() {
        stubGeminiApiClient.setThrowRuntimeException(true);
        DiagnosisForm form = new DiagnosisForm("NORMAL");
        byte[] jpegBytes = "dummy-jpeg-bytes".getBytes(StandardCharsets.UTF_8);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> diagnosisService.executeDiagnosis(form, jpegBytes, "image/jpeg")
        );

        assertEquals("画像の解析に失敗しました。", ex.getMessage());
    }

    private static class StubGeminiApiClient extends GeminiApiClient {
        private boolean throwRuntimeException = false;

        void setThrowRuntimeException(boolean throwRuntimeException) {
            this.throwRuntimeException = throwRuntimeException;
        }

        @Override
        public String sendImageToGemini(Object payload) {
            if (throwRuntimeException) {
                throw new RuntimeException("simulated API error");
            }
            return """
                    推定困難
                    直接確認できる情報: テスト用のダミー応答です。
                    """;
        }
    }
}