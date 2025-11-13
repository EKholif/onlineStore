package com.onlineStore.admin.security.tenant;
// في ملف CurrentTenantIdentifierResolverImpl.java

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

@Component
public class CurrentTenantIdentifierResolverImpl implements CurrentTenantIdentifierResolver {

    // 💡 هام: هذا هو الـ ID الخاص بالمستأجر الافتراضي/الرئيسي (Master)
    // يجب أن يكون ID موجوداً في جدول المستأجرين (Tenants)
    // نستخدم "1" كـ ID افتراضي (Master Tenant ID)
    private static final String MASTER_TENANT_ID_STRING = "0";

    @Override
    public String resolveCurrentTenantIdentifier() {
        // 1. محاولة الحصول على ID من سياق الطلب الحالي (كـ Long)
        Long tenantIdLong = TenantContext.getTenantId();

        if (tenantIdLong != null) {
            // 2. إذا وجد، قم بتحويله إلى String قبل إرجاعه لـ Hibernate
            return tenantIdLong.toString();
        }

        // 3. حالة التهيئة أو عدم وجود ID (أثناء الـ Boot Up)
        // العودة إلى الـ ID الرئيسي/الافتراضي (Master ID) كـ String
        return MASTER_TENANT_ID_STRING;
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}


