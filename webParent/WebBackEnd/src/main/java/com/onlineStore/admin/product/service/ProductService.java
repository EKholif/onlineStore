package com.onlineStore.admin.product.service;


import com.onlineStore.admin.product.repository.ProductRepository;
import com.onlineStore.admin.category.CategoryNotFoundException;
import com.onlineStore.admin.usersAndCustomers.users.servcies.UserService;
import com.onlineStore.admin.category.services.PageInfo;
import com.onlineStoreCom.entity.product.Product;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@Transactional
public class ProductService {

    @Autowired
    private ProductRepository repository;


    public List<Product> listAll() {

        return repository.findAll();
    }

    public Product findById(Integer id) {

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


    public Product saveProduct(Product product) {

        if (product.getId() == null) {
            product.setCreatedTime(new Date());
            product.setUpdatedTime(new Date());
        } else {

            product.setUpdatedTime(new Date());
        }

        product.setAlias(product.getAlias().replace(" ", "_"));

        return repository.save(product);
    }


    public String checkUnique(Integer id, String name, String alias) {
        boolean isCreatingNew = (id == null || id == 0);
        Product productByName = repository.findByName(name);
        Product productByAlias = repository.findByAlias(alias);


        if (productByName != null && !Objects.equals(productByName.getId(), id)) {

            return "DuplicateName";
        }

        if (productByAlias != null && !Objects.equals(productByAlias.getId(), id)) {
            return "DuplicateAlies";
        }

        return "Ok";
    }


    public void UpdateProductEnableStatus(Integer id, Boolean enable) {
        repository.enableProduct(id, enable);

    }

    public List<Product> listByPage(PageInfo pageInfo, int pageNum, String sortField, String sortDir, String keyWord) {

        Sort sort = Sort.by(sortField);


        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1, UserService.USERS_PER_PAGE, sort);

        Page<Product> pageUsers = null;

        if (keyWord != null) {
            pageUsers = repository.findAll(keyWord, pageable);
        } else {
            pageUsers = repository.findAll(pageable);

        }
        pageInfo.setTotalElements(pageUsers.getTotalElements());
        pageInfo.setTotalPages(pageUsers.getTotalPages());


        return pageUsers.getContent();
    }


}
