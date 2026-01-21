package com.onlineStore.admin.helper.abstarct;


import com.onlineStore.admin.helper.PaginationInfo;
import com.onlineStore.admin.helper.SortingInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

public abstract class AbstractPagingHelper<T> implements PagingHelper<T> {

    protected final ModelAndView model;
    protected final String url;
    protected final String pageTitle;
    protected final String listName;
    protected final List<T> listItems;
    protected final Page<T> pageItems;
    protected final String keyword;
    protected final int pageNum;
    protected final int pageSize;
    protected final SortingInfo sorting;

    public AbstractPagingHelper(ModelAndView model, String listName,String pageTitle,String url, List<T> listItems ,Page<T> pageItems, String keyword, int pageNum, int pageSize, SortingInfo sorting) {
        this.model = model;
        this.pageTitle = pageTitle;
        this.listItems = listItems;
        this.url = url;
        this.listName = listName;
        this.pageItems = pageItems;
        this.keyword = keyword;
        this.pageNum = Math.max(1, pageNum);
        this.pageSize = pageSize;
        this.sorting = sorting;
    }

    @Override
    public ModelAndView listByPage() {
        PaginationInfo pagination = new PaginationInfo(pageNum, pageSize, pageItems.getTotalElements());

        addListAttributes();
        addPaginationAttributes(pagination);
        addSortingAttributes();

        model.addObject("search", "/" + url + "/page/1");
        model.addObject("moduleURL", "/" + url + "/page/");
        model.addObject("pageTitle", "List of " + pageTitle);

        return model;
    }
    public AbstractPagingHelper(ModelAndView model,  String listName,String url, String pageTitle, List<T> listItems) {
        this.model = model;
        this.url = url;
        this.pageTitle = pageTitle;
        this.listName = listName;
        this.listItems =listItems;
        this.pageItems = null;
        this.keyword = null;
        this.pageNum = 0;
        this.pageSize = 0;
        this.sorting = null;
    }
    @Override
    public Pageable createPageable() {
        return PageRequest.of(pageNum - 1, pageSize, sorting.getSort());
    }



    @Override
    public ModelAndView newForm(String objectName, T object) {
        model.addObject(objectName, object);
        model.addObject("listItems", listItems);
        model.addObject("pageTitle", "Add New " + pageTitle);
        model.addObject("saveChanges", "/" + url + "/save-new-" + url);

        return model;
    }

    @Override
    public ModelAndView editForm(String objectName, T object, Object id) {
        model.addObject(objectName, object);
        model.addObject("id", id);
        model.addObject("listItems", listItems);
        model.addObject("pageTitle", "Edit " + objectName + " - ID: " + id);
        model.addObject("saveChanges", "/save-edit-" + url + "/" );

        return model;
    }

    protected void addListAttributes() {
        model.addObject(listName, listItems);
        model.addObject("keyWord", keyword);
    }

    protected void addPaginationAttributes(PaginationInfo pagination) {
        model.addObject("startCount", pagination.getStartCount());
        model.addObject("endCount", pagination.getEndCount());
        model.addObject("currentPage", pagination.getCurrentPage());
        model.addObject("totalPages", pagination.getTotalPages());
        model.addObject("totalItems", pagination.getTotalElements());
    }

    protected void addSortingAttributes() {
        model.addObject("sortField", sorting.getSortField());
        model.addObject("sortDir", sorting.getSortDir());
        model.addObject("reverseSortDir", sorting.getReverseSortDir());
    }
}
