package com.example.risk.controller;

import java.io.IOException;
import java.util.HashMap;
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

import com.example.risk.model.DiagnosisForm;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestBody;




@Controller 
public class TestController {
    //TOP画面
    @GetMapping("/")
    public String showTop(Model model) {
        model.addAttribute("diagnosisform",new DiagnosisForm());
        return "top";
    }
    //画像アップロード画面
    @GetMapping("/diagnosis/upload")
    public String showUpload(Model model) {
        model.addAttribute("diagnosisform",new DiagnosisForm());
        return "diagnosis/upload";
    }

    //ローディング画面
    @GetMapping("/diagnosis/progress")
    public String showProgress(HttpSession session){
        if (session.getAttribute("imageData")==null){
            return "redirect:/diagnosis/upload";
        }
        return "diagnosis/progress";
    }

    
    
    //画像アップロード処理
    @PostMapping("/upload")
    @ResponseBody
    public Map<String, Object> upload(
        @ModelAttribute DiagnosisForm form,
        HttpSession session) throws IOException {

        MultipartFile file = form.getImage();
        
        Map<String, Object> response = new HashMap<>();

        response.put("status",true);
        response.put("message","アップロード成功");
        
        
        return response;
    }

    //診断処理
    @PostMapping("/diagnosis/start")
    public String startDiagnosis(HttpSession session) {

    byte[] imageData = (byte[]) session.getAttribute("imageData");

    if (imageData == null) {
        return "redirect:/";
    }

    // 診断処理を実行

    return "diagnosis/result";


    }
    
        
}
