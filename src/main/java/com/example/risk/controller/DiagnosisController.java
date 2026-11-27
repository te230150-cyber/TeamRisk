package com.example.risk.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.risk.model.DiagnosisForm;

import jakarta.servlet.http.HttpSession;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;




@Controller
@RequestMapping("/diagnosis")
public class DiagnosisController {
    
    @GetMapping("/top")
    public String diagnosis_top(Model model) {
        model.addAttribute("diagnosisform",new DiagnosisForm());
        return "diagnosis/upload";
    }

    @PostMapping("upload")
    public String postMethodName(HttpSession session, @ModelAttribute DiagnosisForm diagnosis_form) {
        DiagnosisSession diagnosis_session = new DiagnosisSession();
        session.setAttribute("DiagnosisSession", diagnosis_session);
        
        

        return entity;
    }
    


}
