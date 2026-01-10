package com.onlineStore.admin.debug;

import com.onlineStoreCom.tenant.TenantContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * [AG-DEBUG-001] Runtime Debugger for Tenant Isolation
 * WHY: Proves whether tenantId propagates correctly through the request
 * lifecycle.
 * BUSINESS IMPACT: Prevents data leaks that could expose customer data.
 */
@RestController
@RequestMapping("/debug/tenant-trace")
public class TenantTraceController {

    @GetMapping
    public Map<String, Object> traceTenantContext(HttpServletRequest request) {
        Map<String, Object> trace = new LinkedHashMap<>();

        // Layer 1: Filter/Context
        Long contextTenantId = TenantContext.getTenantId();
        trace.put("1_TenantContext_getTenantId()", contextTenantId);
        trace.put("1_TenantContext_isSet", contextTenantId != null);

        // Layer 2: Session
        HttpSession session = request.getSession(false);
        if (session != null) {
            Object sessionTenantId = session.getAttribute("TENANT_ID");
            trace.put("2_Session_TENANT_ID", sessionTenantId);
        } else {
            trace.put("2_Session_TENANT_ID", "NO_SESSION");
        }

        // Layer 3: Headers
        String headerTenantId = request.getHeader("X-Tenant-ID");
        trace.put("3_Header_X-Tenant-ID", headerTenantId != null ? headerTenantId : "NOT_SET");

        // Layer 4: Request URI
        trace.put("4_Request_URI", request.getRequestURI());
        trace.put("4_Request_Method", request.getMethod());

        // ⚠️ CRITICAL CHECK
        if (contextTenantId == null) {
            trace.put("⚠️_STATUS", "LEAK_RISK: No Tenant ID in Context!");
        } else if (contextTenantId == 0L) {
            trace.put("✅_STATUS", "SAFE: System Tenant (Failsafe Active)");
        } else {
            trace.put("✅_STATUS", "SAFE: Tenant ID = " + contextTenantId);
        }

        return trace;
    }
}
