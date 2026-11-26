package com.example.risk;

import com.example.risk.service.LearningModeService;
import com.example.risk.model.LearningSessionData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class LearningModeServiceTest {

    private LearningModeService learningModeService;
    private MockHttpSession session;

    @BeforeEach
    void setUp() {
        learningModeService = new LearningModeService();
        session = new MockHttpSession();
    }

    @Test
    @DisplayName("5問の自動採点で正解数×2点の得点が算出される")
    void gradeTest_calculatesScore() {
        List<String> correct = List.of("A", "B", "C", "D", "A");
        List<String> answers = List.of("A", "B", "X", "D", "Y"); // 3問正解 → 6点

        int score = learningModeService.gradeTest(answers, correct);

        assertEquals(6, score);
    }

    @Test
    @DisplayName("利用前・利用後テスト得点の保存と比較取得・セッション破棄ができる")
    void sessionScoreLifecycle() {
        LearningSessionData initial = learningModeService.startOrResetSession(session);
        assertNotNull(initial);

        learningModeService.savePreTestScore(session, 4);
        learningModeService.savePostTestScore(session, 8);

        LearningSessionData compared = learningModeService.getTestComparison(session);
        assertEquals(4, compared.getPreTestScore());
        assertEquals(8, compared.getPostTestScore());

        learningModeService.clearSessionData(session);
        assertNull(session.getAttribute(LearningModeService.SESSION_KEY_LEARNING));
    }
}