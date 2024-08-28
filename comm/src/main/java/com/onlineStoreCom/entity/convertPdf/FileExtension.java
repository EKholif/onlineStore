package com.onlineStoreCom.entity.convertPdf;

public enum FileExtension {
    PDF("pdf"),
    DOCX("docx"),
    XLSX("xlsx"),
    JPG("jpg"),
    PNG("png"),
    TXT("txt"),
    HTML("html");

    private final String extension;

    FileExtension(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }
}