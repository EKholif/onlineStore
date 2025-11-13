//package com.onlineStore.admin.test.counteryTest.userTest.tenant;
//
//import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
//import org.springframework.stereotype.Component;
//
//@Component
//public class CurrentTenantResolver implements CurrentTenantIdentifierResolver<String> {
//
//    @Override
//    public String resolveCurrentTenantIdentifier() {
//        Long tenantId = SecurityUtils.getLoggedUserTenantId();
//        return tenantId != null ? tenantId.toString() : "default";
//    }
//
//    @Override
//    public boolean validateExistingCurrentSessions() {
//        return true;
//    }
//}