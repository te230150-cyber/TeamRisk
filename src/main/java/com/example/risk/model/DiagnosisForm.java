package com.example.risk.model;

public class DiagnosisForm {
    private String mode = "NORMAL";

    public DiagnosisForm() {
    }

    public DiagnosisForm(String mode) {
        this.mode = mode;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}