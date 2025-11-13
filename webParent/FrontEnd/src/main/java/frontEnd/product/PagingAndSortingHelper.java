package frontEnd.product;

import frontEnd.category.CategoryService;
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
    private String modelUrl;
    private String pageTitle;
    private Integer totalPages;
    private Integer TotalElements;
     Integer USERS_PER_PAGE = 10;


    private String newObjecturl;
    private int pageNum;
    private List<?> listItems;

    public Page<?> getPageItems() {
        return pageItems;
    }

    public void setPageItems(Page<?> pageItems) {
        this.pageItems = pageItems;
    }

    private Page<?> pageItems;


    public PagingAndSortingHelper(ModelAndView model) {
        this.model = model;
    }

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
        this.search = "/c/" + keyWord + "/page/1";
        this.modelUrl = " /c/" + keyWord + "/page/";
        this.pageTitle =  keyWord;
    }

    public PagingAndSortingHelper() {
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Integer getTotalElements() {
        return TotalElements;
    }

    public void setTotalElements(Integer totalElements) {
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

        model.addObject(name, listItems);
        model.addObject("keyWord", keyWord);
        model.addObject("sortDir", sortDir);
        model.addObject("endCount", endCount);
        model.addObject("currentPage", pageNum);
        model.addObject("sortField", sortField);
        model.addObject("startCont", startCount);
        model.addObject("reverseSortDir", reverseSortDir);
        model.addObject("totalPages", pageInfo.getTotalPages());
        model.addObject("totalItems", pageInfo.getTotalElements());
        model.addObject("search", search);
        model.addObject("modelUrl", modelUrl);
        model.addObject("pageTitle", pageTitle);
        model.addObject("name", "/" + name + "/" + name);

        return model;
    }

    //new object creation constructor
    public PagingAndSortingHelper(String listName, List<?> listItems) {
        this.listName = listName;
        this.listItems = listItems;
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
