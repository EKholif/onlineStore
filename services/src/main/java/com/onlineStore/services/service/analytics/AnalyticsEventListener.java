package com.onlineStore.services.service.analytics;

import com.onlineStoreCom.entity.analytics.event.OrderCompletedEvent;
import com.onlineStoreCom.entity.analytics.event.ProductViewEvent;
import com.onlineStoreCom.entity.analytics.event.SearchEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class AnalyticsEventListener {

    @Autowired
    private AnalyticsService analyticsService;

    @Async
    @EventListener
    public void handleProductView(ProductViewEvent event) {
        analyticsService.recordProductView(event.getTenantId(), event.getProductId());
    }

    @Async
    @EventListener
    public void handleSearch(SearchEvent event) {
        analyticsService.recordSearch(event.getTenantId(), event.getKeyword());
    }

    @Async
    @EventListener
    public void handleOrderCompleted(OrderCompletedEvent event) {
        analyticsService.recordOrder(event.getTenantId(), event.getProductSales());
    }
}
