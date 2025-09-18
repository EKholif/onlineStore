package com.onlineStore.admin.setting.settingBag;

import com.onlineStoreCom.entity.setting.Setting;
import com.onlineStoreCom.entity.setting.SettingBag;

import java.util.List;

public class CurrencySettingBag extends SettingBag {

    public CurrencySettingBag(List<Setting> listSettings) {
        super(listSettings);
    }

    public String getSymbol() {
        return super.getValue("CURRENCY_SYMBOLE");
    }
    public String getSymbolPosition() {
        return super.getValue("CURRENCY_SYMBOLE_POSITION");
    }
    public int getDecimalDigits() {
        return Integer.parseInt(super.getValue("DECIMAL_DIGITS"));
    }
    public String getDecimalPointType() {return super.getValue("DECIMAL_POINT_TYPE");}
    public String getThousandPointType() {return super.getValue("THOUSANDS_POINT_TYPE");}



}
