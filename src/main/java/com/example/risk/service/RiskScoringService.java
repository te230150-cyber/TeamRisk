package com.example.risk.service;

import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class RiskScoringService {

    public record ScoringOutcome(
            int totalScore,
            String riskLevel,
            List<String> factors,
            String reason
    ) {}

    public ScoringOutcome calculate(
            List<String> rawDetectedFactors,
            boolean prefectureEstimated,
            boolean municipalityEstimated
    ) {
        // 同一名称の重複排除（同じ施設・駅・地名が複数回検出されても1回のみ加点）[cite: 8]
        Set<String> uniqueFactors = new LinkedHashSet<>();
        if (rawDetectedFactors != null) {
            for (String factor : rawDetectedFactors) {
                if (factor != null && !factor.isBlank()) {
                    uniqueFactors.add(factor.trim());
                }
            }
        }

        int score = 0;

        // 1. 地域推定レベルの加算（都道府県1点 / 市区町村2点・重複加算なし）[cite: 8]
        if (municipalityEstimated) {
            score += 2;
            uniqueFactors.add("市区町村まで推定可能（2点）");
        } else if (prefectureEstimated) {
            score += 1;
            uniqueFactors.add("都道府県まで推定可能（1点）");
        }

        // 2. 検出要因ごとの配点（同一要因が複数項目に該当する場合は最高配点のみ適用）[cite: 8]
        for (String factor : uniqueFactors) {
            if (factor.endsWith("点）")) {
                continue;
            }
            int factorScore = resolveHighestFactorScore(factor);
            score += factorScore;
        }

        // 3. 危険度判定（8点以上: A判定 / 4〜7点: B判定 / 0〜3点: C判定）[cite: 8]
        String riskLevel;
        if (score >= 8) {
            riskLevel = "A判定";
        } else if (score >= 4) {
            riskLevel = "B判定";
        } else {
            riskLevel = "C判定";
        }

        String reason = String.format(
                "合計リスク点数は %d 点となり、判定基準に基づき「%s」と判定しました。",
                score, riskLevel
        );

        return new ScoringOutcome(score, riskLevel, List.copyOf(uniqueFactors), reason);
    }

    private int resolveHighestFactorScore(String factor) {
        // 学校名など生活圏につながる情報: 4点[cite: 8]
        if (factor.contains("学校") || factor.contains("通学") || factor.contains("生活圏") || factor.contains("自宅")) {
            return 4;
        }
        // 駅名・地名 / 具体的な施設名・店舗名: 3点[cite: 8]
        if (factor.contains("駅") || factor.contains("地名") || factor.contains("施設") || factor.contains("店舗") || factor.contains("店")) {
            return 3;
        }
        // その他、位置特定につながる情報（標識・ランドマーク等）の最低基準[cite: 8]
        return 1;
    }
}