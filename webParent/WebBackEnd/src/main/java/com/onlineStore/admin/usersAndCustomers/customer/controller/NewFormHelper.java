package com.onlineStore.admin.usersAndCustomers.customer.controller;

import org.springframework.web.servlet.ModelAndView;

import java.util.List;

public class NewFormHelper {


    private List<?> listItems;
    private String pageTitle;

    public List<?> getListItems() {
        return listItems;
    }

    public void setListItems(List<?> listItems) {
        this.listItems = listItems;
    }

    public NewFormHelper(List<?> listItems, String pageTitle) {
        this.listItems = listItems;
        this.pageTitle = pageTitle;
    }

    public NewFormHelper(String pageTitle, List<?> listItems) {
        this.listItems = listItems;
        this.pageTitle = pageTitle;
    }




    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public ModelAndView newForm(ModelAndView model, String modelName, Object object) {
        model.addObject("customer", object);
        model.addObject("listItems", listItems);

        model.addObject("pageTitle", pageTitle );
        model.addObject("saveChanges", "/" + modelName + "/save-" + modelName);
        return model;
    }


    public ModelAndView editForm(ModelAndView model, String objectName, Object object, int id ) {
        model.addObject(objectName, object);
        model.addObject("id", id);
        model.addObject("listItems", listItems); // Assuming listItems is a class variable
        model.addObject("pageTitle", pageTitle);
        model.addObject("saveChanges", "/" + objectName + "/save-" + objectName);
        return model;
    }



}
