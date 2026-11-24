package com.example.risk.model;

import java.util.List;

import lombok.Data;

@Data 
public class DiagnosisResult {
    private String estimationStatus;    // 判定結果 ("判定可能" または "推定困難")
    private String estimatedLocation;   // 推定場所（都道府県・市区町村など）
    private String estimationBasis;     // 推定根拠、または特定できなかった理由[cite: 5]
    private String riskLevel;           // 危険度 ("A判定", "B判定", "C判定")[cite: 5]
    private int totalScore;             // リスク合計点[cite: 5]
    private List<String> detectedFactors; // 検出された危険要因のリスト[cite: 5]
    private String judgmentReason;      // 判定理由[cite: 5]

    // Getter / Setter は省略（必要に応じて記述）
}