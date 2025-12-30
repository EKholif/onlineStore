package com.onlineStore.admin.category.controller.utility;

import com.onlineStore.admin.category.services.PageInfo;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PagingAndSorting {


    private PageInfo pageInfo;
    private int pageNum;
    private  String sortField;
    private String sortDir;
    private String keyWord;
    public static final int PRODUCTS_PER_PAGE = 5;


    public PagingAndSorting(PageInfo pageInfo, int pageNum, String sortField, String sortDir, String keyWord) {
        this.pageInfo = pageInfo;
        this.pageNum = pageNum;
        this.sortField = sortField;
        this.sortDir = sortDir;
        this.keyWord = keyWord;

    }

    public PageInfo getPageInfo() {
        return pageInfo;
    }

    public Pageable createPageable() {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
        return PageRequest.of(pageNum - 1, PRODUCTS_PER_PAGE, sort);
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


}
