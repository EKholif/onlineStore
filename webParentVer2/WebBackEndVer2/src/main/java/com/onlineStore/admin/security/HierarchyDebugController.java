package com.onlineStore.admin.security;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HierarchyDebugController {

    // Simple robust endpoint to verify hierarchy works
    @GetMapping("/debug/hierarchy")
    public String debugHierarchy() {
        return "redirect:/"; // Just reload home with new context
    }
}
