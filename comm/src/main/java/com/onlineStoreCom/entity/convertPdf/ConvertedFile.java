package com.onlineStoreCom.entity.convertPdf;


import jakarta.persistence.*;

@Entity
@Table(name = "convertedFile")
public class ConvertedFile {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String fileName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public FileExtension getExtension() {
        return extension;
    }

    public void setExtension(FileExtension extension) {
        this.extension = extension;
    }

    public FileConvert getFileConvert() {
        return fileConvert;
    }

    public void setFileConvert(FileConvert fileConvert) {
        this.fileConvert = fileConvert;
    }

    private FileExtension extension;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private FileConvert fileConvert;


    @Transient
    public String convertedFilePath() {
        String dirName = "/FileConvert/";
        String subDirName = "/convertedFiles/";
        if (id < 0 || fileName == null) return "/images" + "\\" + "bob.png";
        return dirName + this.id + subDirName + this.fileName;
    }

    @Transient
    public String convertedFileDir() {
        String dirName = "/FileConvert/";
        String subDirName = "/convertedFiles/";
        if (id < 0 || fileName == null) return "/images" + "\\" + "bob.png";
        return dirName + this.id + subDirName;
    }





}

