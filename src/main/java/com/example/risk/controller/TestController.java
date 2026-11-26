package com.example.risk.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import com.example.risk.model.DiagnosisForm;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class TestController {
    // test
    @GetMapping("/")
    public String showTop(Model model) {
        model.addAttribute("diagnosisform", new DiagnosisForm());
        return "/quiz/pre-complete";
    }

    // 画像アップロード画面
    @GetMapping("/diagnosis/top")
    public String showUpload(Model model) {
        model.addAttribute("diagnosisform", new DiagnosisForm());
        return "diagnosis/upload";
    }

    // ローディング画面
    @GetMapping("/diagnosis/progress")
    public String showProgress(HttpSession session) {
        return "diagnosis/progress";
    }

    // 利用前テスト画面
    @GetMapping("/quiz/pre")
    public String showPreTest(Model model) {

        model.addAttribute("testType", "pre");
        model.addAttribute("questionNumber", 1);
        model.addAttribute("questionText",
                "下記の写真がAIによってどこまで特定できるか");
        model.addAttribute("imageUrl", "/images/quiz/pre01.jpg");

        return "quiz/test";
    }

    // 利用後テスト画面
    @GetMapping("/quiz/post")
    public String showPostTest(Model model) {

        model.addAttribute("testType", "post");
        model.addAttribute("questionNumber", 1);
        model.addAttribute("questionText",
                "下記の写真がAIによってどこまで特定できるか");
        model.addAttribute("imageUrl", "/images/quiz/post01.jpg");

        return "quiz/test";
    }

    // 画像選択画面
    @GetMapping("/quiz/select")
    public String showSelect(Model model) {

        model.addAttribute("images", List.of(
                Map.of("id", 1, "url", "/images/learning/image01.jpg"),
                Map.of("id", 2, "url", "/images/learning/image02.jpg"),
                Map.of("id", 3, "url", "/images/learning/image03.jpg")));

        return "quiz/select";
    }

    // テスト結果画面
    @GetMapping("/quiz/test-result")
    public String showQuizResult(Model model) {

        // 診断前後の得点
        model.addAttribute("preTestScore", 4);
        model.addAttribute("postTestScore", 8);

        // 利用前テストの回答結果
        model.addAttribute("preTestResult", List.of(
                Map.of(
                        "userAnswer", "特定できない",
                        "correctAnswer", "地区まで特定",
                        "correct", false),
                Map.of(
                        "userAnswer", "地区まで特定",
                        "correctAnswer", "市区町村まで特定",
                        "correct", false),
                Map.of(
                        "userAnswer", "B",
                        "correctAnswer", "C",
                        "correct", false),
                Map.of(
                        "userAnswer", "市区町村まで特定",
                        "correctAnswer", "市区町村まで特定",
                        "correct", true),
                Map.of(
                        "userAnswer", "D",
                        "correctAnswer", "D",
                        "correct", true)));

        // 利用後テストの回答結果
        model.addAttribute("postTestResult", List.of(
                Map.of(
                        "userAnswer", "特定できない",
                        "correctAnswer", "特定できない",
                        "correct", true),
                Map.of(
                        "userAnswer", "市区町村まで特定",
                        "correctAnswer", "市区町村まで特定",
                        "correct", true),
                Map.of(
                        "userAnswer", "D",
                        "correctAnswer", "D",
                        "correct", true),
                Map.of(
                        "userAnswer", "B",
                        "correctAnswer", "B",
                        "correct", true),
                Map.of(
                        "userAnswer", "地区まで特定",
                        "correctAnswer", "市区町村まで特定",
                        "correct", false)));

        return "quiz/test-result";
    }

    // 画像アップロード処理
    @PostMapping("diagnosis/upload")
    @ResponseBody
    public Map<String, Object> upload(
            @ModelAttribute DiagnosisForm form,
            HttpSession session) throws IOException {

        // MultipartFile file = form.getImage();

        Map<String, Object> response = new HashMap<>();

        response.put("status", true);
        response.put("message", "アップロード成功");

        return response;
    }

    // 診断処理
    @PostMapping("/diagnosis/result")
    public String startDiagnosis(HttpSession session) {

        

        return "diagnosis/result";

    }

}
