package com.onlineStore.admin.setting.settingBag;

import com.onlineStoreCom.entity.setting.Setting;
import com.onlineStoreCom.entity.setting.SettingBag;

import java.util.List;

public class PaymentSettingBag extends SettingBag {

    private String currencySymbole;

    public PaymentSettingBag(List<Setting> listSettings) {
        super(listSettings);
    }

    public String getSymbol() {
        return super.getValue("CURRENCY_SYMBOLE");
    }
    public String getPayPalApiBaseUrl() {
        return super.getValue("PAYPAL_API_BASE_URL");
    }
    public String getPayPalAPiClientId() {return super.getValue("PAYPAL_API_CLIENT_ID");}
    public String getPayPAlApiClientSecret() {return super.getValue("PAYPAL_API_CLIENT_SECRET");}



}
