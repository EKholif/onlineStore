package com.onlineStore.admin.setting.service;

import com.onlineStore.admin.setting.country.SettingRepository;
import com.onlineStore.admin.setting.settingBag.FrontendSettingBag;
import com.onlineStoreCom.entity.setting.Setting;
import com.onlineStoreCom.entity.setting.SettingCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FrontendSettingsService {

    @Autowired
    private SettingRepository settingRepo;

    public FrontendSettingBag getFrontendSettings() {
        List<Setting> settings = settingRepo.findByCategory(SettingCategory.FRONTEND);
        return new FrontendSettingBag(settings);
    }

    public void saveAll(Iterable<Setting> settings) {
        settingRepo.saveAll(settings);
    }
}
