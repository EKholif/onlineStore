package com.onlineStore.admin.product.service;

import com.onlineStore.admin.category.services.PageInfo;
import com.onlineStore.admin.product.repository.ProductRepository;
import com.onlineStoreCom.entity.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public class ListProductByCategory {

    private PageInfo pageInfo;
    private int pageNum;
    private  String sortField;
    private String sortDir;
    private String keyWord;
    private String alise;

    private ProductRepository productRepository;
    public static final int PRODUCTS_PER_PAGE = 5;

    public ListProductByCategory(PageInfo pageInfo, int pageNum, String sortField, String sortDir, String keyWord) {
        this.pageInfo = pageInfo;
        this.pageNum = pageNum;
        this.sortField = sortField;
        this.sortDir = sortDir;
        this.keyWord = keyWord;


    }

    public PageInfo getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public String getSortDir() {
        return sortDir;
    }

    public void setSortDir(String sortDir) {
        this.sortDir = sortDir;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }


    public ProductRepository getProductRepository() {
        return productRepository;
    }

    public void setProductRepository(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> listByCategory(PageInfo pageInfo, int pageNum, String sortField, String sortDir, String keyWord, String alise) {

        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1, PRODUCTS_PER_PAGE, sort);

        Page<Product> pageUsers = null;

        if (keyWord != null) {

            pageUsers = productRepository.findAll(keyWord, pageable);


        } else {
            pageUsers = productRepository.findAll(pageable);
        }

        pageInfo.setTotalElements(pageUsers.getTotalElements());
        pageInfo.setTotalPages(pageUsers.getTotalPages());

        return pageUsers.getContent();
    }

    public List<Product> listByCategory() {

        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1, PRODUCTS_PER_PAGE, sort);

        Page<Product> pageUsers = null;

        if (keyWord != null) {

            pageUsers = productRepository.findAll(keyWord, pageable);


        } else {
            pageUsers = productRepository.findAll(pageable);
        }

        pageInfo.setTotalElements(pageUsers.getTotalElements());
        pageInfo.setTotalPages(pageUsers.getTotalPages());

        return pageUsers.getContent();
    }






}
