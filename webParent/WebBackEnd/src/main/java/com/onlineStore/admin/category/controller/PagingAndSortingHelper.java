package com.onlineStore.admin.category.controller;

import com.onlineStore.admin.category.services.CategoryService;
import com.onlineStore.admin.category.services.PageInfo;
import com.onlineStoreCom.entity.User;
import com.onlineStoreCom.entity.prodact.Category;
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

    private String search  ;
    private String modelUrl ;
    private String pageTitle ;

    private String newObjecturl ;
    private int pageNum;
    private List<?> listItems;

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
        this.listItems =  listItems;
        this.search   =   "/"+listName+"/page/1";
        this.modelUrl = " /"+listName+"/page/";
        this.pageTitle ="List  "+listName;

        this.newObjecturl = listName+"/new-"+listName+"-form";

    }
    public PagingAndSortingHelper(ModelAndView model, String listName,
                                  String sortField, String sortDir, String keyWord,
                                  int pageNum,Page<?> pageItems) {
        this.pageNum=pageNum;
        this.model = model;
        this.listName = listName;
        this.sortField = sortField;
        this.sortDir = sortDir;
        this.keyWord = keyWord;
        this.pageItems= pageItems;
        this.listItems =  pageItems.getContent();
        this.search   =   "/"+listName+"/page/1";
        this.modelUrl = " /"+listName+"/page/";
        this.pageTitle ="List  "+listName;

        this.newObjecturl = listName+"/new-"+listName+"-form";
    }
    public ModelAndView listByPage( PageInfo pageInfo) {


        long startCount = (long) (pageNum - 1) * CategoryService.USERS_PER_PAGE + 1;
        long endCount = startCount + CategoryService.USERS_PER_PAGE - 1;



        if (endCount > pageInfo.getTotalElements()) {
            endCount = pageInfo.getTotalElements();
        }

        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

        model.addObject("keyWord", keyWord);
        model.addObject("sortDir" , sortDir);
        model.addObject("endCount", endCount);
        model.addObject("currentPage", pageNum);
        model.addObject("sortFiled" , sortField);
        model.addObject("startCont", startCount);
        model.addObject("categories", listItems);
        model.addObject("reverseSortDir", reverseSortDir);
        model.addObject("totalPages",  pageInfo.getTotalPages());
        model.addObject("totalItems", pageInfo.getTotalElements());
        model.addObject("search" , search);
        model.addObject("modelUrl", modelUrl);
        model.addObject("pageTitle",pageTitle );

        return  model;
    }
    public ModelAndView listByPage( ) {

        PageInfo pageInfo = new PageInfo();
        long startCount = (long) (pageNum - 1) * CategoryService.USERS_PER_PAGE + 1;
        long endCount = startCount + CategoryService.USERS_PER_PAGE - 1;



        if (endCount > pageItems.getTotalElements()) {
            endCount = pageItems.getTotalElements();
        }

        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

        model.addObject("keyWord", keyWord);
        model.addObject("sortDir" , sortDir);
        model.addObject("endCount", endCount);
        model.addObject("currentPage", pageNum);
        model.addObject("sortFiled" , sortField);
        model.addObject("startCont", startCount);
        model.addObject("users", listItems);
        model.addObject("reverseSortDir", reverseSortDir);
        model.addObject("totalPages",  pageItems.getTotalPages());
        model.addObject("totalItems", pageItems.getTotalElements());
        model.addObject("search" , search);
        model.addObject("modelUrl", modelUrl);
        model.addObject("pageTitle",pageTitle );

        return  model;
    }

    public ModelAndView listByPage(Page<User> listByPage ) {


       setListItems(listByPage.getContent()) ;


        long startCount = (long) (pageNum - 1) * CategoryService.USERS_PER_PAGE + 1;
        long endCount = startCount + CategoryService.USERS_PER_PAGE - 1;


        if (endCount > listByPage.getTotalElements()) {
            endCount = listByPage.getTotalElements();
        }

        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

        model.addObject("keyWord", keyWord);
        model.addObject("sortDir" , sortDir);
        model.addObject("endCount", endCount);
        model.addObject("currentPage", pageNum);
        model.addObject("sortFiled" , sortField);
        model.addObject("startCont", startCount);
        model.addObject("categories", listItems);
        model.addObject("reverseSortDir", reverseSortDir);
        model.addObject("totalPages",  listByPage.getTotalPages());
        model.addObject("totalItems", listByPage.getTotalElements());
        model.addObject("search" , search);
        model.addObject("modelUrl", modelUrl);
        model.addObject("pageTitle",pageTitle );

        return  model;
    }

    public PagingAndSortingHelper(String listName, List<?> listItems) {
        this.listName = listName;
        this.listItems = listItems;
    }

    //    public void listEntities(int pageNum, int pageSize, SearchRepository<?, Integer> repo) {
//        Pageable pageable = createPageable(pageSize, pageNum);
//        Page<?> page = null;
//
//        if (keyWord != null) {
//            page = repo.findAll(keyWord, pageable);
//        } else {
//            page = repo.findAll(pageable);
//        }
//
//        updateModelAttributes(pageNum, page);
//    }

    public Pageable createPageable(int pageSize, int pageNum) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
        return PageRequest.of(pageNum - 1, pageSize, sort);
    }



    public ModelAndView newForm(ModelAndView model, Category object) {
        model.addObject("object", object);
        model.addObject("listItems", listItems); // Assuming listItems is a class variable
        model.addObject("pageTitle", "Create new " + listName);
        model.addObject("saveChanges", "/" + listName + "/save-category");
        return model;
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
