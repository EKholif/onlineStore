package com.onlineStore.admin.order;

import com.onlineStore.admin.product.service.ProductService;
import com.onlineStore.admin.utility.paging.PagingAndSortingHelper;
import com.onlineStore.admin.utility.paging.PagingAndSortingParam;
import com.onlineStoreCom.entity.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ProductSearchController {

    @Autowired
    private ProductService service;

	@GetMapping("/orders/search-form/search_product")
	public String showSearchProductPage() {
		return "orders/search-form/search_product";
	}

	@PostMapping("/orders/search-form/search_product")
	public String searchProducts(String keyword) {
		return "redirect:/orders/search-form/search_product/page/1?sortField=name&sortDir=asc&keyWord=" + keyword;
	}

	@GetMapping("/orders/search-form/search_product/page/{pageNum}")
    public String searchProductsByPage(
            @PagingAndSortingParam(listName = "listProducts", moduleURL = "/orders/search-form/search_product") PagingAndSortingHelper helper,
			@PathVariable(name = "pageNum") int pageNum) {
        Page<Product> page = service.listByPage(pageNum, helper.getSortField(), helper.getSortDir(),
                helper.getKeyword());
        helper.updateModelAttributes(pageNum, page);
		return "orders/search-form/search_product";
	}
}
