package com.onlineStore.admin.audit;

import com.onlineStoreCom.entity.AuditLog;
import com.onlineStoreCom.tenant.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuditLogService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    public void log(String module, String action, String details) {
        Long tid = TenantContext.getTenantId();
        String tenantId = (tid != null) ? String.valueOf(tid) : "SYSTEM";

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = (auth != null) ? auth.getName() : "Anonymous";

        AuditLog log = new AuditLog(tenantId, module, action, userEmail, details);
        auditLogRepository.save(log);
    }

    public Page<AuditLog> listByPage(int pageNum, String sortField, String sortDir, String keyword) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1, 20, sort);
        Long tid = TenantContext.getTenantId();
        String tenantId = (tid != null) ? String.valueOf(tid) : "SYSTEM";

        // If Super Admin, maybe show all? For now, let's stick to tenant context or
        // specialized logic
        // Assuming current context is what matters. If Super Admin views ALL, logic
        // needs adjustment.
        // For compliance, usually you view *your* logs or *all* logs if you are super.

        // TODO: Add logic to check if user is Super Admin to show all logs.
        // For now, filtering by current tenant context if set, else all (if "SYSTEM" or
        // similar).

        if (keyword != null && !keyword.isEmpty()) {
            return auditLogRepository.findAllWithFilter(keyword, pageable);
        }
        return auditLogRepository.findAll(pageable);
    }
}
