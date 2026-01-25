package com.onlineStore.admin.order.event;

import com.onlineStoreCom.entity.order.Order;
import org.springframework.context.ApplicationEvent;

public class OrderCompletedEvent extends ApplicationEvent {

    private final Order order;
    private final Long tenantId;

    public OrderCompletedEvent(Object source, Order order, Long tenantId) {
        super(source);
        this.order = order;
        this.tenantId = tenantId;
    }

    public Order getOrder() {
        return order;
    }

    public Long getTenantId() {
        return tenantId;
    }
}
