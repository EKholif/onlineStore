package com.onlineStore.admin.category.controller;

import com.onlineStore.admin.category.controller.utility.PagingAndSorting;
import com.onlineStore.admin.category.services.CategoryService;
import com.onlineStore.admin.category.services.PageInfo;
import com.onlineStoreCom.entity.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

public class PagingAndSortingHelper {


    private ModelAndView model;
    private String listName;
    private String sortField;
    private String sortDir;
    private String keyWord;

    private String search;
    private String moduleURL;
    private String pageTitle;
    private Long totalPages;
    private Long TotalElements;
    private PageInfo pageInfo;

    private String newObjecturl;
    private int pageNum;
    private List<?> listItems;

    public PagingAndSortingHelper(ModelAndView model, String products, List<Product> listByPage, PagingAndSorting listProductByCategory) {
        this.model = model;
        this.listName = products;
        this.sortField = listProductByCategory.getSortField();
        this.sortDir = listProductByCategory.getSortDir();
        this.keyWord = listProductByCategory.getKeyWord();
        this.pageNum = listProductByCategory.getPageNum();
        this.pageInfo = listProductByCategory.getPageInfo();
        this.listItems = listByPage;
        this.search = "/" + products + "/page/1";
        this.moduleURL = " /" + products + "/page/";
        this.pageTitle = "List  " + products;
        this.newObjecturl = products + "/new-" + products + "-form";
    }

    public PagingAndSortingHelper(ModelAndView model, String products, List<Product> listByPage) {
        this.model = model;
        this.listName = products;
        this.listItems = listByPage;
        this.search = "/" + products + "/page/1";
        this.moduleURL = " /" + products + "/page/";
        this.pageTitle = "List  " + products;
        this.newObjecturl = products + "/new-" + products + "-form";


    }

    public Page<?> getPageItems() {
        return pageItems;
    }

    public void setPageItems(Page<?> pageItems) {
        this.pageItems = pageItems;
    }

    private Page<?> pageItems;


    public PagingAndSortingHelper(ModelAndView model, String listName,
                                  String sortField, String sortDir, String keyWord,
                                  int pageNum, List<?> listItems) {

        this.model = model;
        this.listName = listName;
        this.sortField = sortField;
        this.sortDir = sortDir;
        this.keyWord = keyWord;
        this.pageNum = pageNum;
        this.listItems = listItems;
        this.search = "/" + listName + "/page/1";
        this.moduleURL = " /" + listName + "/page/";
        this.pageTitle = "List  " + listName;
        this.newObjecturl = listName + "/new-" + listName + "-form";

    }

    public PagingAndSortingHelper() {
    }

    public Long getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Long totalPages) {
        this.totalPages = totalPages;
    }

    public Long getTotalElements() {
        return TotalElements;
    }

    public void setTotalElements(Long totalElements) {
        TotalElements = totalElements;
    }


// to list the data By page

    public ModelAndView listByPage(PageInfo pageInfo, String name) {

        long startCount = (long) (pageNum - 1) * CategoryService.USERS_PER_PAGE + 1;
        long endCount = startCount + CategoryService.USERS_PER_PAGE - 1;

        if (endCount > pageInfo.getTotalElements()) {
            endCount = pageInfo.getTotalElements();
        }


        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

        model.addObject(listName, listItems);
        model.addObject("name", name+"/"+name);

//        model.addObject(name, listItems);
        model.addObject("keyWord", keyWord);
        model.addObject("sortDir", sortDir);
        model.addObject("endCount", endCount);
        model.addObject("currentPage", pageNum);
        model.addObject("sortField", sortField);
        model.addObject("startCount", startCount);
        model.addObject("reverseSortDir", reverseSortDir);
        model.addObject("totalPages", pageInfo.getTotalPages());
        model.addObject("totalItems", pageInfo.getTotalElements());
        model.addObject("search", search);
        model.addObject("moduleURL", moduleURL);
        model.addObject("pageTitle", pageTitle);

        return model;
    }

    //new object creation constructor
    public PagingAndSortingHelper(String listName, List<?> listItems) {
        this.listName = listName;
        this.listItems = listItems;
    }

    public ModelAndView newForm(ModelAndView model, String objectName, Object object) {
        model.addObject(objectName, object);
        model.addObject("listItems", listItems); // Assuming listItems is a class variable
        model.addObject("pageTitle", "Create new " + objectName);
        model.addObject("saveChanges", "/" + listName + "/save-" + objectName);
        return model;
    }

    public ModelAndView editForm(ModelAndView model, String objectName, Object object, int id) {
        model.addObject(objectName, object);
        model.addObject("id", id);

        model.addObject("listItems", listItems); // Assuming listItems is a class variable
        model.addObject("pageTitle", " Edit : " + objectName + " ID :  " + id);
        model.addObject("saveChanges", "/" + listName + "/save-edit-" + objectName);
        return model;
    }

    public ModelAndView editForm(ModelAndView model, String objectName, Object object, Long id) {
        model.addObject(objectName, object);
        model.addObject("id", id);
        model.addObject("listItems", listItems); // Assuming listItems is a class variable
        model.addObject("pageTitle", " Edit : " + objectName + " ID :  " + id);

        model.addObject("saveChanges", "/" + listName + "/save-edit-" + objectName + "/");
        return model;
    }


    public Pageable createPageable(int pageSize, int pageNum) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
        return PageRequest.of(pageNum - 1, pageSize, sort);
    }


    public ModelAndView getModel() {
        return model;
    }

    public void setModel(ModelAndView model) {
        this.model = model;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
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

    public String getkeyWord() {
        return keyWord;
    }

    public void setkeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public List<?> getListItems() {
        return listItems;
    }

    public void setListItems(List<?> listItems) {
        this.listItems = listItems;
    }
}
