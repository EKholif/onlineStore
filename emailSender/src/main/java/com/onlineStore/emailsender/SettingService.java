package com.onlineStore.emailsender;

import com.onlineStoreCom.entity.setting.Setting;
import com.onlineStoreCom.entity.setting.SettingCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class SettingService {
	@Autowired private SettingRepository settingRepo;

	public GeneralSettingBag getGeneralSettings() {
		List<Setting> settings = settingRepo.findByTwoCategories(SettingCategory.GENERAL, SettingCategory.CURRENCY);

		return new GeneralSettingBag(settings);
	}
	public void saveAll(Iterable<Setting> settings) {
		settingRepo.saveAll(settings);
	}


    public List<Setting> settingList(){
        return settingRepo.findAll();
    }
	public List<Setting> getMailServerSettings() {
		return settingRepo.findByCategory(SettingCategory.MAIL_SERVER);
	}
	public List<Setting> getMailTemplateSettings() {
		return settingRepo.findByCategory(SettingCategory.MAIL_TEMPLATES);
	}

	public EmailSettingBag getEmailSettings() {
		List<Setting> settings = settingRepo.findByCategory(SettingCategory.MAIL_SERVER);
		settings.addAll(settingRepo.findByCategory(SettingCategory.MAIL_TEMPLATES));

		return new EmailSettingBag(settings);
	}

}
