package frontEnd.setting;

import com.onlineStoreCom.entity.setting.Setting;
import com.onlineStoreCom.entity.setting.SettingCategory;
import frontEnd.setting.repository.SettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class DynamicCssController {

    @Autowired
    private SettingRepository settingRepository;

    @GetMapping(value = "/css/custom_theme.css", produces = "text/css")
    @ResponseBody
    public String getCustomThemeCss() {
        Long tenantId = com.onlineStoreCom.tenant.TenantContext.getTenantId();
        System.out.println("DYNAMIC CSS FILTER: Current Tenant ID: " + tenantId);

        List<Setting> themeSettings = settingRepository.findByCategory(SettingCategory.THEME);
        System.out.println("DYNAMIC CSS FILTER: Found " + themeSettings.size() + " theme settings.");

        StringBuilder css = new StringBuilder(":root {\n");

        String primaryColor = "#007bff"; // Default
        String secColor = "#6c757d"; // Default
        String fontFamily = "'Inter', sans-serif"; // Default

        for (Setting setting : themeSettings) {
            if ("THEME_COLOR".equals(setting.getKey())) {
                primaryColor = setting.getValue();
            } else if ("THEME_SEC_COLOR".equals(setting.getKey())) {
                secColor = setting.getValue();
            } else if ("THEME_FONT_FAMILY".equals(setting.getKey())) {
                fontFamily = setting.getValue();
            }
        }

        if (themeSettings.isEmpty()) {
            System.out.println("DYNAMIC CSS: No settings found. Serving SYSTEM DEFAULTS (" + primaryColor + ")");
        }

        // Map to standard CSS variables used in style.css
        // Assuming style.css uses --primary-color, --secondary-color,
        // --font-family-base
        css.append("  --primary-color: ").append(primaryColor).append(" !important;\n");
        css.append("  --secondary-color: ").append(secColor).append(" !important;\n");
        css.append("  --font-family-sans-serif: ").append(fontFamily).append(" !important;\n");

        // Additional overrides for specific Bootstrap classes if variables aren't
        // enough
        css.append("}\n");

        css.append("body { font-family: var(--font-family-sans-serif); }\n");
        css.append(".bg-primary { background-color: var(--primary-color) !important; }\n");
        css.append(".text-primary { color: var(--primary-color) !important; }\n");
        css.append(
                ".btn-primary { background-color: var(--primary-color) !important; border-color: var(--primary-color) !important; }\n");
        css.append(".bg-secondary { background-color: var(--secondary-color) !important; }\n");

        return css.toString();
    }
}
