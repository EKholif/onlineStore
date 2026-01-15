package frontEnd.debug;

import com.onlineStoreCom.entity.setting.Setting;
import com.onlineStoreCom.tenant.TenantContext;
import frontEnd.setting.service.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class DebugController {

    @Autowired
    private SettingService settingService;

    @GetMapping("/debug/theme")
    public Map<String, Object> debugTheme(jakarta.servlet.http.HttpServletRequest request) {
        Long tenantId = TenantContext.getTenantId();
        List<Setting> settings = settingService.getThemeSettings();

        return Map.of(
                "serverName", request.getServerName(),
                "localPort", request.getLocalPort(),
                "currentTenantId", tenantId == null ? "NULL" : tenantId,
                "settingsCount", settings.size(),
                "settings", settings.stream().collect(Collectors.toMap(Setting::getKey, Setting::getValue)));
    }
}
