package com.example.risk.model;

import org.springframework.web.multipart.MultipartFile;

public class DiagnosisForm {

    // 1. 通常診断モード用：アップロードされた画像ファイル（SC002）
    private MultipartFile image;

    // 2. 診断モード区分（"NORMAL": 通常診断モード / "LEARNING": 学習・評価モード）
    private String mode;

    // 3. 学習・評価モード用：あらかじめ用意された学習用画像の識別ID（SC006）
    private String sampleImageId;

    // --- 入力チェック用メソッド（設計書 5.3項番4 / 7.3.2対応） ---

    /**
     * 画像が空（未選択）か判定する
     */
    public boolean isImageEmpty() {
        return image == null || image.isEmpty();
    }

    /**
     * ファイル形式が JPEG または PNG か判定する
     */
    public boolean isValidContentType() {
        if (isImageEmpty()) return false;
        String contentType = image.getContentType();
        return "image/jpeg".equals(contentType) || "image/png".equals(contentType);
    }

    /**
     * ファイルサイズが 10MB 以下か判定する（10MB = 10 * 1024 * 1024 バイト）
     */
    public boolean isValidFileSize() {
        if (isImageEmpty()) return false;
        long maxSizeBytes = 10L * 1024 * 1024;
        return image.getSize() <= maxSizeBytes;
    }

    // --- Getter / Setter ---

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getSampleImageId() {
        return sampleImageId;
    }

    public void setSampleImageId(String sampleImageId) {
        this.sampleImageId = sampleImageId;
    }
}