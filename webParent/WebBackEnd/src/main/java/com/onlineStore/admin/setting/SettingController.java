package com.onlineStore.admin.setting;

import com.onlineStore.admin.setting.repository.CurrencyRepository;
import com.onlineStore.admin.setting.service.SettingService;
import com.onlineStore.admin.setting.settingBag.GeneralSettingBag;
import com.onlineStore.admin.utility.FileUploadUtil;
import com.onlineStoreCom.entity.setting.Setting;
import com.onlineStoreCom.entity.setting.subsetting.Currency;
import com.onlineStoreCom.tenant.TenantContext;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
public class SettingController {

    @Autowired
    private SettingService service;

    @Autowired

    private CurrencyRepository currencyRepo;

    @GetMapping("/settings")
    public String listAll(Model modal) {

        List<Setting> settingList = service.settingList();
        List<Currency> currencyList = currencyRepo.findAllByOrderByNameAsc();
        for (Setting setting : settingList) {
            modal.addAttribute(setting.getKey(), setting.getValue());
        }

        modal.addAttribute("currencyList", currencyList);
        return "settings/settings";

    }

    @PostMapping("/settings/save_general")
    public String saveGeneralSettings(@RequestParam("fileImage") MultipartFile multipartFile,
                                      HttpServletRequest request, RedirectAttributes ra) throws IOException {
        GeneralSettingBag settingBag = service.getGeneralSettings();

        Long tenantId = TenantContext.getTenantId();
        settingBag.setTenantId(tenantId);

        saveSiteLogo(multipartFile, settingBag);
        saveCurrencySymbol(request, settingBag);

        updateSettingValuesFromForm(request, settingBag.list());

        ra.addFlashAttribute("message", "General settings have been saved.");

        return "redirect:/settings";
    }

    private void saveSiteLogo(MultipartFile multipartFile, GeneralSettingBag settingBag) throws IOException {
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            String value = "/site-logo/" + fileName;

            Long tenantId = TenantContext.getTenantId();
            settingBag.setTenantId(tenantId);

            settingBag.updateSiteLogo(value);
            String uploadDir = "site-logo";
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

        }
    }

    private void saveCurrencySymbol(HttpServletRequest request, GeneralSettingBag settingBag) {
        Integer currencyId = Integer.parseInt(request.getParameter("CURRENCY_ID"));
        Optional<Currency> findByIdResult = currencyRepo.findById(String.valueOf(currencyId));

        if (findByIdResult.isPresent()) {
            Currency currency = findByIdResult.get();
            settingBag.updateCurrencySymbol(currency.getSymbol());

        }
    }

    private void updateSettingValuesFromForm(HttpServletRequest request, List<Setting> listSettings) {
        for (Setting setting : listSettings) {
            String value = request.getParameter(setting.getKey());
            if (value != null) {
                setting.setValue(value);
            }
        }

        service.saveAll(listSettings);
    }

    @PostMapping("/settings/save_mail_server")
    public String saveMailServerSetttings(HttpServletRequest request, RedirectAttributes ra) {
        List<Setting> mailServerSettings = service.getMailServerSettings();

        Long tenantId = TenantContext.getTenantId();
        for (Setting setting : mailServerSettings) {
            setting.setTenantId(tenantId);
        }
        ;

        updateSettingValuesFromForm(request, mailServerSettings);

        ra.addFlashAttribute("message", "Mail server settings have been saved");

        return "redirect:/settings#mailServer";
    }

    @PostMapping("/settings/save_mail_template")
    public String saveMailTemplateSetttings(HttpServletRequest request, RedirectAttributes ra) {

        List<Setting> mailTemplateSettings = service.getMailTemplateSettings();
        updateSettingValuesFromForm(request, mailTemplateSettings);

        ra.addFlashAttribute("message", "Mail Template have been saved");

        return "redirect:/settings#mailServer";
    }

    @PostMapping("/settings/save_payment")

    public String savePaymentSettings(HttpServletRequest request, RedirectAttributes ra) {
        List<Setting> paymentSettings = service.getPaymentSettings();
        updateSettingValuesFromForm(request, paymentSettings);

        ra.addFlashAttribute("message", "Payment settings have been saved");

        return "redirect:/settings#payment";
    }

}