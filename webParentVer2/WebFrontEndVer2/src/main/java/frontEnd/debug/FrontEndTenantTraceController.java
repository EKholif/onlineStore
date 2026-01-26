package frontEnd.debug;

import com.onlineStoreCom.entity.setting.Setting;
import com.onlineStoreCom.tenant.TenantContext;
import frontEnd.setting.service.SettingService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * [AG-DEBUG-002] FrontEnd Runtime Debugger for Settings/Theme Leakage
 * WHY: Proves whether Settings queries are tenant-scoped or returning mixed
 * data.
 * BUSINESS IMPACT: Theme leakage breaks customer trust and brand isolation.
 */
@RestController
@RequestMapping("/debug/tenant-settings")
public class FrontEndTenantTraceController {

    @Autowired
    private SettingService settingService;

    @GetMapping
    public Map<String, Object> traceSettingsResolution(HttpServletRequest request) {
        Map<String, Object> trace = new LinkedHashMap<>();

        // Layer 1: Context
        Long contextTenantId = TenantContext.getTenantId();
        trace.put("1_TenantContext_getTenantId()", contextTenantId);

        // Layer 2: Service Call (the CRITICAL layer where leaks occur)
        List<Setting> settings = settingService.getGenlSettings();

        // Layer 3: Analyze Results
        Map<Long, Long> tenantIdCounts = settings.stream()
                .collect(Collectors.groupingBy(
                        s -> s.getTenantId() != null ? s.getTenantId() : -1L,
                        Collectors.counting()));

        trace.put("2_Settings_Retrieved_Count", settings.size());
        trace.put("2_Settings_By_TenantId", tenantIdCounts);

        // ⚠️ CRITICAL: Detect Leakage
        if (tenantIdCounts.size() > 1) {
            trace.put("⚠️_STATUS", "LEAK DETECTED: Multiple tenants in result set!");
            trace.put("⚠️_EXPECTED_TENANT", contextTenantId);
            trace.put("⚠️_ACTUAL_TENANTS", tenantIdCounts.keySet());
        } else if (contextTenantId != null && tenantIdCounts.containsKey(contextTenantId)) {
            trace.put("✅_STATUS", "SAFE: All settings match Context Tenant");
        } else if (contextTenantId == 0L && tenantIdCounts.containsKey(0L)) {
            trace.put("✅_STATUS", "SAFE: System settings (Failsafe Active)");
        } else {
            trace.put("⚠️_STATUS", "MISMATCH: Context vs Query Tenant IDs differ");
        }

        return trace;
    }
}
