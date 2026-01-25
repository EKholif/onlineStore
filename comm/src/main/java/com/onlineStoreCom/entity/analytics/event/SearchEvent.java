package com.onlineStoreCom.entity.analytics.event;

public class SearchEvent {
    private final String keyword;
    private final Integer tenantId;

    public SearchEvent(String keyword, Integer tenantId) {
        this.keyword = keyword;
        this.tenantId = tenantId;
    }

    public String getKeyword() {
        return keyword;
    }

    public Integer getTenantId() {
        return tenantId;
    }
}
