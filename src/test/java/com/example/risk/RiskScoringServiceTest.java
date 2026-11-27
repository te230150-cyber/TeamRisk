package com.example.risk;

import com.example.risk.service.RiskScoringService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RiskScoringServiceTest {

    private RiskScoringService riskScoringService;

    @BeforeEach
    void setUp() {
        riskScoringService = new RiskScoringService();
    }

    @Test
    @DisplayName("要因なし・地域推定なしの場合は0点でC判定")
    void calculate_noFactors_cLevel() {
        RiskScoringService.ScoringOutcome outcome = riskScoringService.calculate(List.of(), false, false);

        assertEquals(0, outcome.totalScore());
        assertEquals("C判定", outcome.riskLevel());
        assertTrue(outcome.factors().isEmpty());
    }

    @Test
    @DisplayName("重複要因の除外と地域推定・要因配点の加算（B判定：4〜7点）")
    void calculate_deduplicationAndBLevel() {
        // 「〇〇駅」(3点)が重複 → 1回分3点 + 都道府県推定(1点) = 4点 → B判定
        RiskScoringService.ScoringOutcome outcome = riskScoringService.calculate(
                List.of("〇〇駅", " 〇〇駅 ", ""),
                true,
                false
        );

        assertEquals(4, outcome.totalScore());
        assertEquals("B判定", outcome.riskLevel());
        assertTrue(outcome.factors().contains("〇〇駅"));
        assertTrue(outcome.factors().contains("都道府県まで推定可能（1点）"));
    }

    @Test
    @DisplayName("市区町村推定と高配点要因で8点以上となりA判定")
    void calculate_municipalityAndHighScore_aLevel() {
        // 市区町村推定(2点) + 〇〇学校(4点) + 道路標識(1点) = 7点 → B判定
        RiskScoringService.ScoringOutcome bOutcome = riskScoringService.calculate(
                List.of("〇〇学校", "道路標識"),
                true,
                true
        );
        assertEquals(7, bOutcome.totalScore());
        assertEquals("B判定", bOutcome.riskLevel());

        // さらに「△△店舗」(3点)を追加 → 10点 → A判定
        RiskScoringService.ScoringOutcome aOutcome = riskScoringService.calculate(
                List.of("〇〇学校", "△△店舗", "道路標識"),
                true,
                true
        );
        assertEquals(10, aOutcome.totalScore());
        assertEquals("A判定", aOutcome.riskLevel());
    }
}