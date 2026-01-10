package com.onlineStoreCom.entity.setting;

import com.onlineStoreCom.entity.setting.subsetting.IdBasedEntity;

import java.util.List;

public class SettingBag extends IdBasedEntity {
    private final List<Setting> listSettings;

    public SettingBag(List<Setting> listSettings) {
        this.listSettings = listSettings;
    }

    public Setting get(String key) {
        Setting defaultSetting = null;

        for (Setting setting : listSettings) {
            if (setting.getKey().equals(key)) {
                // If we find a tenant-specific setting (non-zero/non-null tenant), return it
                // immediately (it overrides default)
                // Assuming '0' is system default.
                Long tid = setting.getTenantId();
                if (tid != null && tid != 0) {
                    return setting;
                }
                // Otherwise keep it as a candidate for default
                defaultSetting = setting;
            }
        }

        return defaultSetting;
    }

    public String getValue(String key) {
        Setting setting = get(key);
        if (setting != null) {
            return setting.getValue();
        }

        return null;
    }

    public void update(String key, String value) {
        Setting setting = get(key);
        if (setting != null && value != null) {
            setting.setValue(value);
        }
    }

    public List<Setting> list() {
        return listSettings;
    }

    public List<Setting> getAllSettings() {
        return this.listSettings;
    }

}
