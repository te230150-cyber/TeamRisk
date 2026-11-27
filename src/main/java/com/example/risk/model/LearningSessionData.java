package com.example.risk.model;

public class LearningSessionData {
    private Integer preTestScore;   // 利用前テスト得点（0〜10点）[cite: 5]
    private Integer postTestScore;  // 利用後テスト得点（0〜10点）[cite: 5]

    /**
     * 得点差（利用後得点 － 利用前得点）を算出する[cite: 5]
     */
    public int getScoreDifference() {
        if (postTestScore == null || preTestScore == null) {
            return 0;
        }
        return postTestScore - preTestScore;
    }

    // Getter / Setter は省略
}