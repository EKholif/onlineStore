package com.onlineStore.admin.feature;

import com.onlineStoreCom.entity.FeatureFlag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/system/features")
public class FeatureToggleController {

    @Autowired
    private FeatureFlagService featureFlagService;

    @GetMapping
    public String listFeatures(Model model) {
        List<FeatureFlag> features = featureFlagService.listGlobalFlags();
        model.addAttribute("features", features);
        return "system/features";
    }

    @PostMapping("/save")
    public String saveFeature(FeatureFlag featureFlag, RedirectAttributes ra) {
        featureFlagService.save(featureFlag);
        ra.addFlashAttribute("message", "Feature saved successfully.");
        return "redirect:/admin/system/features";
    }

    @GetMapping("/delete/{id}")
    public String deleteFeature(@PathVariable("id") Long id, RedirectAttributes ra) {
        featureFlagService.delete(id);
        ra.addFlashAttribute("message", "Feature deleted successfully.");
        return "redirect:/admin/system/features";
    }

    @GetMapping("/toggle/{id}")
    public String toggleFeature(@PathVariable("id") Long id, RedirectAttributes ra) {
        FeatureFlag flag = featureFlagService.get(id);
        if (flag != null) {
            flag.setEnabled(!flag.isEnabled());
            featureFlagService.save(flag);
            ra.addFlashAttribute("message", "Feature toggled successfully.");
        }
        return "redirect:/admin/system/features";
    }
}
