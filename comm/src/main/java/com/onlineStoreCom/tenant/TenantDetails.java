package com.onlineStoreCom.tenant;

import java.io.Serializable;

/**
 * [AG-TEN-REQ-002] Holds minimal Tenant details for the Context.
 * Used to pass Tenant info (ID for DB, Name for Domain/UI) across the system.
 */
public class TenantDetails implements Serializable {
    private Long id;
    private String name; // Domain or Subdomain
    private String schema; // Optional: for separate schema strategy later

    public TenantDetails(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "TenantDetails{id=" + id + ", name='" + name + "'}";
    }
}
