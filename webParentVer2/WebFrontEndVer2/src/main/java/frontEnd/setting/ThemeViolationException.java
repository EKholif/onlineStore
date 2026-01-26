package frontEnd.setting;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE, reason = "Frontend Theme Compliance Violation: No valid theme configuration found for tenant.")
public class ThemeViolationException extends RuntimeException {

    private final Long tenantId;

    public ThemeViolationException(String message, Long tenantId) {
        super(message);
        this.tenantId = tenantId;
    }

    public Long getTenantId() {
        return tenantId;
    }
}
