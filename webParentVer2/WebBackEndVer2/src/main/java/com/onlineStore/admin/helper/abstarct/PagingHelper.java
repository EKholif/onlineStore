package com.onlineStore.admin.helper.abstarct;

import org.springframework.data.domain.Pageable;
import org.springframework.web.servlet.ModelAndView;

public interface PagingHelper<T> {
    ModelAndView listByPage();

    ModelAndView newForm(String objectName, T object);

    ModelAndView editForm(String objectName, T object, Object id);

    Pageable createPageable();
}
