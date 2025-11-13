package frontEnd.setting.service;


import com.onlineStoreCom.entity.setting.Setting;
import com.onlineStoreCom.entity.setting.SettingCategory;
import com.onlineStoreCom.entity.setting.subsetting.Currency;
import frontEnd.setting.EmailSettingBag;
import frontEnd.setting.repository.CurrencyRepository;
import frontEnd.setting.repository.GeneralSettingBag;
import frontEnd.setting.repository.SettingRepository;
import frontEnd.setting.settingBag.CurrencySettingBag;
import frontEnd.setting.settingBag.PaymentSettingBag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class SettingService {
	@Autowired private SettingRepository settingRepo;
	@Autowired private CurrencyRepository currencyRepo;

	public GeneralSettingBag getGeneralSettings() {
		List<Setting> settings = settingRepo.findByTwoCategories(SettingCategory.GENERAL, SettingCategory.CURRENCY);

		return new GeneralSettingBag(settings);
	}

	public List<Setting> getGenlSettings() {
		List<Setting> settings = settingRepo.findByTwoCategories(SettingCategory.GENERAL, SettingCategory.CURRENCY);

		return  settings;
	}

	public void saveAll(Iterable<Setting> settings) {
		settingRepo.saveAll(settings);
	}


    public List<Setting> settingList(){
        return settingRepo.findAll();
    }


	public EmailSettingBag getEmailSettings() {
		List<Setting> settings = settingRepo.findByCategory(SettingCategory.MAIL_SERVER);
		settings.addAll(settingRepo.findByCategory(SettingCategory.MAIL_TEMPLATES));
		settings.addAll(settingRepo.findByCategory(SettingCategory.GENERAL));

		return new EmailSettingBag(settings);
	}

	public CurrencySettingBag getCurrencySettings() {
		List<Setting> settings = settingRepo.findByCategory(SettingCategory.CURRENCY);
		return new CurrencySettingBag(settings);
	}

	public PaymentSettingBag getPaymentSettings() {
		List<Setting> settings = settingRepo.findByCategory(SettingCategory.PAYMENT);
		return new PaymentSettingBag(settings);
	}

	public String getCurrencyCode() {
		Setting setting = settingRepo.findByKey("CURRENCY_ID");
		Integer currencyId = Integer.parseInt(setting.getValue());
		String currency = currencyRepo.getReferenceById(String.valueOf(currencyId)).getCode();

		return currency;
	}


}
