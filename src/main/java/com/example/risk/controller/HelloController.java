package com.example.risk.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloController {

    @GetMapping("/")
    @ResponseBody
    public String index() {
        return "<h1>Hello, Spring Boot Web Server!</h1><p>プロジェクトの動作確認用ページです。</p>";
    }
}