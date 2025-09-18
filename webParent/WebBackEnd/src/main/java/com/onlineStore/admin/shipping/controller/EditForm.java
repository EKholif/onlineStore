package com.onlineStore.admin.shipping.controller;

import com.onlineStore.admin.helper.abstarct.AbstractPagingHelper;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

public class EditForm  <T>extends AbstractPagingHelper<T> {
    public EditForm(ModelAndView model, String listName, String url, String pageTitle, List<T> listItems) {
        super(model, listName, url, pageTitle, listItems);
    }
}
