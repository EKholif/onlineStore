package com.onlineStore.admin.product.service;


import com.onlineStore.admin.category.CategoryNotFoundException;
import com.onlineStore.admin.product.repository.ProductDetailsRepository;
import com.onlineStoreCom.entity.product.ProductDetails;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
public class ProductDetailsService {

    @Autowired
    private ProductDetailsRepository repository;


    public List<ProductDetails> listAll() {

        return repository.findAll();
    }


    public Optional<ProductDetails> listByProduct(int id ) {

        return repository.findById(id);
    }

    public ProductDetails findById(Integer id) {

        return repository.getReferenceById(id);
    }

    public Boolean existsById(Integer id) {
        return repository.findById(id).isPresent();
    }


    public void deleteProduct(Integer id) throws CategoryNotFoundException {
        try {
            repository.deleteById(id);

        } catch (NoSuchElementException ex) {

            throw new CategoryNotFoundException("Could not find any Category with ID " + id);
        }
    }


}