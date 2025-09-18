package com.onlineStore.admin.setting.settingBag;

import com.onlineStoreCom.entity.setting.Setting;
import com.onlineStoreCom.entity.setting.SettingBag;

import java.util.List;



public class GeneralSettingBag extends SettingBag {

	public GeneralSettingBag(List<Setting> listSettings) {
		super(listSettings);
	}

	public void updateCurrencySymbol(String value) {
		super.update("CURRENCY_SYMBOLE", value);
	}
	
	public void updateSiteLogo(String value) {
		super.update("SITE_LOGO", value);
	}


}



