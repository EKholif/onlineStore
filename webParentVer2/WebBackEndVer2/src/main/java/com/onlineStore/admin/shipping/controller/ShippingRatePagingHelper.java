package com.onlineStore.admin.shipping.controller;

import com.onlineStore.admin.helper.SortingInfo;
import com.onlineStore.admin.helper.abstarct.AbstractPagingHelper;
import com.onlineStoreCom.entity.setting.state.Country.Country;
import com.onlineStoreCom.entity.shipping.ShippingRate;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.data.domain.Page;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

public class ShippingRatePagingHelper extends AbstractPagingHelper<ShippingRate> {

    public ShippingRatePagingHelper(ModelAndView model, String listName, String pageTitle, String url, List<ShippingRate> listItems, Page<ShippingRate> pageItems, String keyword, int pageNum, int pageSize, SortingInfo sorting) {
        super(model, "shippingRates", pageTitle, url, listItems, pageItems, keyword, pageNum, pageSize, sorting);
    }


}
