package frontEnd.setting;

import com.onlineStoreCom.entity.setting.Setting;
import com.onlineStoreCom.entity.setting.SettingCategory;
import com.onlineStoreCom.tenant.TenantContext;
import frontEnd.setting.repository.SettingRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(true)
public class ThemePriorityIntegrationTest {

    @Autowired
    private SettingRepository repo;

    @Test
    public void testGoldenRule_SpecificUserOverridesDefault() {
        // [AG-THEME-RULE-002] Enforcing Hierarchy Priority

        // 1. Setup Context for Tenant 5
        Long tenantId = 5L;
        TenantContext.setTenantId(tenantId);

        try {
            // 2. Fetch Settings (Should include both 0 and 5)
            List<Setting> settings = repo.findByCategory(SettingCategory.FRONTEND_THEME);

            // 3. Verify Sort Order logic (Dependency for "Last Write Wins")
            // The list MUST be sorted by tenantId ASC (0 first, 5 last)

            boolean foundDefault = false;
            boolean foundSpecific = false;
            int defaultIndex = -1;
            int specificIndex = -1;

            for (int i = 0; i < settings.size(); i++) {
                Setting s = settings.get(i);
                if (s.getKey().equals("FRONTEND_THEME_COLOR_PRIMARY")) {
                    if (s.getTenantId() == 0) {
                        foundDefault = true;
                        defaultIndex = i;
                    } else if (s.getTenantId().equals(tenantId)) {
                        foundSpecific = true;
                        specificIndex = i;
                    }
                }
            }

            Assertions.assertTrue(foundDefault, "Golden Rule Violation: Default (Tenant 0) setting missing!");
            Assertions.assertTrue(foundSpecific, "Golden Rule Violation: Specific (Tenant 5) setting missing!");

            // CRITICAL CHECK: Default must appear BEFORE Specific for "Last Key Wins" logic
            Assertions.assertTrue(defaultIndex < specificIndex,
                    "Golden Rule Violation: Sort Order is WRONG! Specific Tenant settings must load AFTER Defaults to override them. " +
                            "Current: Default@" + defaultIndex + ", Specific@" + specificIndex);

            System.out.println("✅ Golden Rule #2 (Hierarchy Priority) Passed: Specific Overrides Default.");

        } finally {
            TenantContext.clear();
        }
    }
}
