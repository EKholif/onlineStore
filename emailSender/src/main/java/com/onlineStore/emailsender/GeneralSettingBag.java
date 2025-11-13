package com.onlineStore.emailsender;

import com.onlineStoreCom.entity.setting.Setting;
import com.onlineStoreCom.entity.setting.SettingBag;

import java.util.List;


public class GeneralSettingBag extends SettingBag {

	public GeneralSettingBag(List<Setting> listSettings) {
		super(listSettings);
	}

	public String getSiteName() {
		return super.getValue("SITE_NAME");
	}
}



