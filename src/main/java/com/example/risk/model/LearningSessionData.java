package com.example.risk.model; // 実際のパッケージ名に合わせてください

import lombok.Data;

@Data 
public class LearningSessionData {
    private int preTestScore;
    private int postTestScore;
}