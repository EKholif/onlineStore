package frontEnd.setting;

import com.onlineStoreCom.entity.setting.Setting;
import frontEnd.setting.service.SettingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;

@Component
public class ThemeGuardInterceptor implements HandlerInterceptor {

    @Autowired
    private SettingService settingService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        String path = request.getRequestURI();

        // Exclude static resources and error pages to avoid loops
        if (path.startsWith("/css/") || path.startsWith("/js/") || path.startsWith("/images/")
                || path.startsWith("/webjars/") || path.equals("/error") || path.startsWith("/debug/")) {
            return true;
        }

        // Exclude login/register if needed, but Theme should apply there too.
        // For now, strict: Theme invalid -> nothing works.

        boolean themeExists = false;
        try {
            // Check for existence of THEME settings
            // Effectively checking: "Does this tenant have a theme configured?"
            List<Setting> themeSettings = settingService.getThemeSettings();

            // Loose check: If we have ANY theme settings, we consider it valid.
            // Strict check (Future): Verify specific keys like THEME_COLOR exists.
            if (themeSettings != null && !themeSettings.isEmpty()) {
                themeExists = true;
            }
        } catch (Exception e) {
            // DB error or Service error -> Fail closed.
            System.err.println("Theme Guard Probe Failed: " + e.getMessage());
        }

        if (!themeExists) {
            Long tenantId = com.onlineStoreCom.tenant.TenantContext.getTenantId();
            // [AG-FE-THEME-004] RELAXED PROTOCOL: "Make Themes Default Value"
            // Instead of blocking, we log a warning and let DynamicCssController serve
            // defaults.
            System.out.println(
                    "⚠️ THEME GUARD WARNING: Tenant [" + tenantId + "] has no theme. Falling back to System Defaults.");

            // Allow request to proceed
            return true;
        }

        return true;
    }
}
