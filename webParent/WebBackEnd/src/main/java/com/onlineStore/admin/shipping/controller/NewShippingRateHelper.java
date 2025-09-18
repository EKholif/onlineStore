package com.onlineStore.admin.shipping.controller;

import com.onlineStore.admin.helper.SortingInfo;
import com.onlineStore.admin.helper.abstarct.AbstractPagingHelper;
import com.onlineStoreCom.entity.setting.state.Country.Country;
import com.onlineStoreCom.entity.shipping.ShippingRate;
import org.springframework.data.domain.Page;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

public class NewShippingRateHelper <T>extends AbstractPagingHelper<T> {


    public NewShippingRateHelper(ModelAndView model,String listName, String url, String pageTitle,
                                  List<T> listItems) {
        super(model, listName, url, pageTitle,listItems);}


    // Add custom logic if needed

}
