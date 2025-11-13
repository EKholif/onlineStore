package com.onlineStore.admin.security.tenant;
import java.util.UUID;

public class TenantService {

//    create a random tenant id for new tenant

    public static Long createTenant() {
        return Math.abs(new java.util.Random().nextLong());
    }
}
