package com.onlineStore.admin.security.tenant;
import java.util.UUID;

public class TenantService {

    public static Long createTenant() {
        return Math.abs(new java.util.Random().nextLong());
    }
}
