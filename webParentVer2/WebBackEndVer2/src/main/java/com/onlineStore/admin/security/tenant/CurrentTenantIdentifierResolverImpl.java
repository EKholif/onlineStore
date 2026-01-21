package com.onlineStore.admin.security.tenant;

import com.onlineStoreCom.tenant.TenantContext;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

@Component
public class CurrentTenantIdentifierResolverImpl implements CurrentTenantIdentifierResolver {

    @Override
    public String resolveCurrentTenantIdentifier() {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId != null) {
            return tenantId.toString();
        }
        // default tenant (e.g., demo)
        return "0";
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}
