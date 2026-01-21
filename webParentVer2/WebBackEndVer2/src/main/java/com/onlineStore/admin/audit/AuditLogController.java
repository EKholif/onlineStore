package com.onlineStore.admin.audit;

import com.onlineStoreCom.entity.AuditLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/admin/system")
public class AuditLogController {

    @Autowired
    private AuditLogService auditLogService;

    @GetMapping("/audit-logs")
    public String viewAuditLogs(Model model) {
        return viewAuditLogsPage(1, "timestamp", "desc", null, model);
    }

    @GetMapping("/audit-logs/page")
    public String viewAuditLogsPage(@RequestParam(name = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(name = "sortField", defaultValue = "timestamp") String sortField,
            @RequestParam(name = "sortDir", defaultValue = "desc") String sortDir,
            @RequestParam(name = "keyword", required = false) String keyword,
            Model model) {

        Page<AuditLog> page = auditLogService.listByPage(pageNum, sortField, sortDir, keyword);
        List<AuditLog> listLogs = page.getContent();

        long startCount = (long) (pageNum - 1) * 20 + 1;
        long endCount = startCount + 20 - 1;
        if (endCount > page.getTotalElements()) {
            endCount = page.getTotalElements();
        }

        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("listLogs", listLogs);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("keyword", keyword);
        model.addAttribute("module", "audit-logs"); // For pagination links

        return "system/audit_logs";
    }
}
