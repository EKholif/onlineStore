package com.onlineStore.admin.setting;

import com.onlineStore.admin.setting.service.FrontendSettingsService;
import com.onlineStore.admin.setting.settingBag.FrontendSettingBag;
import com.onlineStoreCom.entity.setting.Setting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/settings/frontend")
public class FrontendSettingsRestController {

    @Autowired
    private FrontendSettingsService service;

    @GetMapping
    public Map<String, String> getFrontendSettings() {
        FrontendSettingBag settings = service.getFrontendSettings();
        List<Setting> listSettings = settings.list();

        Map<String, String> resultMap = new HashMap<>();
        for (Setting setting : listSettings) {
            resultMap.put(setting.getKey(), setting.getValue());
        }

        return resultMap;
    }
}
