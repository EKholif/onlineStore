package frontEnd.setting.service;

import com.onlineStoreCom.entity.setting.DefaultTheme;
import com.onlineStoreCom.entity.setting.Setting;
import com.onlineStoreCom.entity.setting.SettingCategory;
import com.onlineStoreCom.entity.setting.ThemeDTO;
import frontEnd.setting.repository.SettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ThemeService {

    @Autowired
    private SettingRepository settingRepo;

    public ThemeDTO getResolvedTheme() {
        // 1. Fetch Tenant Settings (Repository is already tenant-aware via
        // TenantContext/Filter)
        List<Setting> tenantSettings = settingRepo.findByCategory(SettingCategory.THEME);

        // 2. Prepare Map for Resolution
        Map<String, String> resolved = new HashMap<>(DefaultTheme.SETTINGS); // Start with Defaults (Immutable source)

        // 3. Overlay Tenant Settings
        for (Setting s : tenantSettings) {
            if (s.getValue() != null && !s.getValue().isEmpty()) {
                resolved.put(s.getKey(), s.getValue());
            }
        }

        // 4. Map to DTO
        return mapToDTO(resolved);
    }

    private ThemeDTO mapToDTO(Map<String, String> map) {
        return new ThemeDTO(
                map.get("THEME_COLOR_PRIMARY"),
                map.get("THEME_COLOR_SECONDARY"),
                map.get("THEME_HEADER_BG"),
                map.get("THEME_HEADER_COLOR"),
                map.get("THEME_FOOTER_BG"),
                map.get("THEME_FOOTER_COLOR"),
                map.get("THEME_FONT_FAMILY"),
                map.get("THEME_FONT_SIZE"),
                map.get("THEME_FONT_WEIGHT"));
    }
}
