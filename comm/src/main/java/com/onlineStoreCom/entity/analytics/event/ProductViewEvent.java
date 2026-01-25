package com.onlineStoreCom.entity.analytics.event;

public class ProductViewEvent {
    private final Integer productId;
    private final Integer tenantId;

    public ProductViewEvent(Integer productId, Integer tenantId) {
        this.productId = productId;
        this.tenantId = tenantId;
    }

    public Integer getProductId() {
        return productId;
    }

    public Integer getTenantId() {
        return tenantId;
    }
}
