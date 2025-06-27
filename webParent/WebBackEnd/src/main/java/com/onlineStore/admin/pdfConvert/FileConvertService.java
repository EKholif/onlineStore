package com.onlineStore.admin.pdfConvert;


import com.onlineStore.admin.category.CategoryNotFoundException;
import com.onlineStoreCom.entity.convertPdf.FileConvert;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class FileConvertService {

    @Autowired
    private FileConvertRepository repository;



     public FileConvert save (FileConvert fileConvert){
       return  repository.save(fileConvert);
     }

    public List<FileConvert> listAll() {

        return repository.findAll();
    }

    public FileConvert findById(Integer id) {

        return repository.getReferenceById(id);
    }

    public Boolean existsById(Integer id) {
        return repository.findById(id).isPresent();
    }


    public void deleteFileConvert(Integer id) throws CategoryNotFoundException {
        try {
            repository.deleteById(id);

        } catch (NoSuchElementException ex) {

            throw new CategoryNotFoundException("Could not find any Category with ID " + id);
        }
    }











}
