package com.example.risk.model;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data 
public class DiagnosisForm {
    private MultipartFile image;
}
