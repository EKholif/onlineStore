package com.onlineStore.admin.pdfConvert;


import com.onlineStoreCom.entity.convertPdf.FileConvert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface FileConvertRepository extends JpaRepository<FileConvert, Integer> {







}
