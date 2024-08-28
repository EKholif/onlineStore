package com.onlineStoreCom.entity.convertPdf;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "fileConvert")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class FileConvert {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String filename;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")  // Correct use of @JoinColumn
    private FileConvert fileConvert;

    @OneToMany(mappedBy = "fileConvert", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<FileConvert> convertedFiles = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private FileExtension extension;

    public FileExtension getExtension() {
        return extension;
    }

    public void setExtension(FileExtension extension) {
        this.extension = extension;
    }



    public FileConvert() {
    }

    public FileConvert(Integer id, String filename, FileConvert fileConvert, Set<FileConvert> convertedFiles) {
        this.id = id;
        this.filename = filename;
        this.fileConvert = fileConvert;
        this.convertedFiles = convertedFiles;
    }

    @Transient
    public String getFilePath() {
        String dirName = "/FileConvert/";


        if (id < 0 || fileConvert == null) return "/images" + "\\" + "bob.png";

        return dirName + this.id + '\\' + this.fileConvert;
    }


    @Transient
    public String getFileDir() {
        String fileConvert = "FileConvert\\";
        if (id < 0 || fileConvert == null) return "\\images \\ bob.png";
        return fileConvert + this.id + '\\';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public FileConvert getFileConvert() {
        return fileConvert;
    }

    public void setFileConvert(FileConvert fileConvert) {
        this.fileConvert = fileConvert;
    }

    public Set<FileConvert> getConvertedFiles() {
        return convertedFiles;
    }

    public void setConvertedFiles(Set<FileConvert> convertedFiles) {
        this.convertedFiles = convertedFiles;
    }
}
