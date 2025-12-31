package com.onlineStore.admin.setting.settingBag;

import com.onlineStoreCom.entity.setting.Setting;
import com.onlineStoreCom.entity.setting.SettingBag;

import java.util.List;

public class ThemeSettingBag extends SettingBag {

    public ThemeSettingBag(List<Setting> listSettings) {
        super(listSettings);
    }

    // Helper methods to get specific theme values if needed,
    // but the generic getValue from SettingBag might suffice for now.
    // We can add getters for specific colors if we want type safety later.
}
