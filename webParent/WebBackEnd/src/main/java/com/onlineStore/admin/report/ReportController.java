package com.onlineStore.admin.report;

import com.onlineStore.admin.setting.service.SettingService;
import com.onlineStore.admin.setting.settingBag.CurrencySettingBag;
import com.onlineStoreCom.entity.setting.Setting;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class ReportController {

    @Autowired
    private SettingService settingService;

    @GetMapping("/reports")
    public String viewSalesReportHome(HttpServletRequest request) {
        loadCurrencySetting(request);
        return "reports/reports";
    }

    private void loadCurrencySetting(HttpServletRequest request) {
        CurrencySettingBag currencySettings = settingService.getCurrencySettings();

        for (Setting s : currencySettings.getAllSettings()) {
            request.setAttribute(s.getKey(), s.getValue());
        }
    }
}
