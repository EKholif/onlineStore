package com.onlineStore.admin.setting;

import com.onlineStore.admin.setting.service.RewardService;
import com.onlineStoreCom.entity.setting.RewardSetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/settings/rewards")
public class RewardSettingController {

    @Autowired
    private RewardService rewardService;

    @GetMapping
    public String viewRewardSettings(Model model) {
        RewardSetting setting = rewardService.getSettings();
        model.addAttribute("rewardSetting", setting);
        model.addAttribute("pageTitle", "Reward Settings");
        return "settings/reward_settings";
    }

    @PostMapping("/save")
    public String saveRewardSettings(RewardSetting setting, RedirectAttributes redirectAttributes) {
        rewardService.saveSettings(setting);
        redirectAttributes.addFlashAttribute("message", "Reward settings have been saved successfully.");
        return "redirect:/settings/rewards";
    }
}
