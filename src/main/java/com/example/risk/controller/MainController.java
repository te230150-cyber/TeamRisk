package com.example.risk.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainController {

    @GetMapping({"","/","/top"})
    public String top() {
        return "top";
    }

    @GetMapping("/diagnosis")
    public String diagnosis() {
        return "redirect:/diagnosis/top";
    }

    @GetMapping("/quiz")
    public String quiz() {
        return "redirect:/quiz/top";
    }
}