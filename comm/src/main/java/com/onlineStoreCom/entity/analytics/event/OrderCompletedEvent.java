package com.onlineStoreCom.entity.analytics.event;

public class OrderCompletedEvent {
    private final Integer orderId;
    private final Integer tenantId;
    private final Double totalAmount;
    private final java.util.Map<Integer, Double> productSales;

    public OrderCompletedEvent(Integer orderId, Integer tenantId, Double totalAmount, java.util.Map<Integer, Double> productSales) {
        this.orderId = orderId;
        this.tenantId = tenantId;
        this.totalAmount = totalAmount;
        this.productSales = productSales;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public Integer getTenantId() {
        return tenantId;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public java.util.Map<Integer, Double> getProductSales() {
        return productSales;
    }
}
