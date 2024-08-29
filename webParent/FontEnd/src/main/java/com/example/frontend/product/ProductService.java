package com.example.frontend.product;


import com.onlineStoreCom.entity.product.Product;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@Transactional
public class ProductService {

    @Autowired
    private ProductRepository repository;


    public List<Product> listAll() {

        return repository.findAll();
    }

    public Set<Product> setAll(Long categoryId) {

        return  repository.setProductByCategory(categoryId);
    }




    public Product findById(Integer id) {

        return repository.getReferenceById(id);
    }

    public Boolean existsById(Integer id) {
        return repository.findById(id).isPresent();
    }








}
