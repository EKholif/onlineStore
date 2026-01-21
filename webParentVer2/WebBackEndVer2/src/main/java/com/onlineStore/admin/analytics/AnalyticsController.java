package com.onlineStore.admin.analytics;

import com.onlineStore.admin.order.OrderRepository;
import com.onlineStoreCom.entity.order.Order;
import com.onlineStore.admin.setting.service.SettingService;
import com.onlineStore.admin.setting.settingBag.CurrencySettingBag;
import com.onlineStoreCom.entity.setting.Setting;
import com.onlineStoreCom.entity.setting.SettingCategory;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/admin/dashboard/analytics")
public class AnalyticsController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private SettingService settingService;

    @Autowired
    private com.onlineStore.admin.setting.country.SettingRepository settingRepository;

    @Autowired
    private com.onlineStore.admin.tracking.VisitorSessionRepository visitorSessionRepo;

    @Autowired
    private com.onlineStore.admin.tracking.ActivityTracker activityTracker;

    @GetMapping
    public String viewAnalytics(Model model, HttpServletRequest request) {
        fixLegacyCurrencySettings(); // Auto-fix typo in DB if present
        loadCurrencySetting(request);

        // Summary Cards
        Long totalOrders = orderRepository.countTotalOrders();
        Double totalSales = orderRepository.sumTotalSales();
        if (totalSales == null)
            totalSales = 0.0;

        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalSales", totalSales);

        // Active Traffic (Real-time from DB - Last 5 mins)
        Calendar calActive = Calendar.getInstance();
        calActive.add(Calendar.MINUTE, -5);
        Long realTimeVisitors = visitorSessionRepo.countByLastActiveTimeAfter(calActive.getTime());

        model.addAttribute("activeVisitors", realTimeVisitors);

        // Use activityTracker
        model.addAttribute("activeUsers", activityTracker.getActiveUserCount());
        model.addAttribute("activeCustomers", activityTracker.getActiveCustomerCount());

        // Chart Data: Last 7 Days
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -7);
        Date startDate = cal.getTime();
        Date endDate = new Date();

        List<Order> recentOrders = orderRepository.findByOrderTimeBetween(startDate, endDate);

        // Prepare data for Chart.js
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        model.addAttribute("recentOrdersCount", recentOrders.size());

        return "analytics/dashboard";
    }

    private void apiFixLegacyCurrencySettings() {
        List<Setting> settings = settingRepository.findByCategory(SettingCategory.CURRENCY);

        for (Setting s : settings) {
            boolean dirty = false;
            if (s.getKey().equals("CURRENCY_SYMBOLE")) {
                s.setKey("CURRENCY_SYMBOL");
                dirty = true;
            }
            if (s.getKey().equals("CURRENCY_SYMBOLE_POSITION")) {
                s.setKey("CURRENCY_SYMBOL_POSITION");
                dirty = true;
            }
            if (s.getKey().equals("CURRENCY_SYMBOL_POSITION")) {
                if ("Before Price".equals(s.getValue())) {
                    s.setValue("Before price");
                    dirty = true;
                } else if ("After Price".equals(s.getValue())) {
                    s.setValue("After price");
                    dirty = true;
                }
            }
            if (dirty) {
                settingRepository.save(s);
            }
        }
    }

    private void fixLegacyCurrencySettings() {
        try {
            apiFixLegacyCurrencySettings();
        } catch (Exception e) {
            System.err.println("Error fixing currency settings: " + e.getMessage());
        }
    }

    private void loadCurrencySetting(HttpServletRequest request) {
        CurrencySettingBag currencySettings = settingService.getCurrencySettings();

        for (Setting setting : currencySettings.list()) {
            request.setAttribute(setting.getKey(), setting.getValue());
        }
    }
}
