package com.example.risk.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.risk.model.DiagnosisForm;

import org.springframework.ui.Model;



@Controller
@RequestMapping("/diagnosis")
public class DiagnosisController {
    
    @GetMapping("/top")
    public String diagnosis_top(Model model) {
        model.addAttribute("diagnosisform",new DiagnosisForm());
        return "diagnosis/upload";
    }


}
