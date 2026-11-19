package com.example.risk.service;

import com.example.risk.model.LearningSessionData;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LearningModeService {

    public static final String SESSION_KEY_LEARNING = "LEARNING_SESSION_DATA";
    private static final int POINTS_PER_QUESTION = 2; // 1問2点・5問で10点満点[cite: 8]

    /**
     * 学習・評価モード開始時に前回のテスト得点を初期化する[cite: 8]
     */
    public LearningSessionData startOrResetSession(HttpSession session) {
        LearningSessionData data = new LearningSessionData();
        session.setAttribute(SESSION_KEY_LEARNING, data);
        return data;
    }

    /**
     * 5問の回答を自動採点し、得点（0〜10点）を算出する[cite: 8]
     */
    public int gradeTest(List<String> userAnswers, List<String> correctAnswers) {
        if (userAnswers == null || correctAnswers == null) {
            return 0;
        }
        int correctCount = 0;
        int questionCount = Math.min(5, correctAnswers.size());
        for (int i = 0; i < questionCount; i++) {
            if (i < userAnswers.size() && correctAnswers.get(i).equals(userAnswers.get(i))) {
                correctCount++;
            }
        }
        return correctCount * POINTS_PER_QUESTION;
    }

    /**
     * 利用前テスト（SC005）の得点をセッションに保存する[cite: 8]
     */
    public void savePreTestScore(HttpSession session, int score) {
        LearningSessionData data = getOrCreateSessionData(session);
        data.setPreTestScore(score);
        session.setAttribute(SESSION_KEY_LEARNING, data);
    }

    /**
     * 利用後テスト（SC007）の得点をセッションに保存する[cite: 8]
     */
    public void savePostTestScore(HttpSession session, int score) {
        LearningSessionData data = getOrCreateSessionData(session);
        data.setPostTestScore(score);
        session.setAttribute(SESSION_KEY_LEARNING, data);
    }

    /**
     * テスト結果比較画面（SC008）向けのセッションデータを取得する[cite: 8]
     */
    public LearningSessionData getTestComparison(HttpSession session) {
        return getOrCreateSessionData(session);
    }

    /**
     * 学習・評価モード終了時にセッション内のテスト得点を破棄する[cite: 8]
     */
    public void clearSessionData(HttpSession session) {
        if (session != null) {
            session.removeAttribute(SESSION_KEY_LEARNING);
        }
    }

    private LearningSessionData getOrCreateSessionData(HttpSession session) {
        Object attr = session.getAttribute(SESSION_KEY_LEARNING);
        if (attr instanceof LearningSessionData data) {
            return data;
        }
        LearningSessionData newData = new LearningSessionData();
        session.setAttribute(SESSION_KEY_LEARNING, newData);
        return newData;
    }
}