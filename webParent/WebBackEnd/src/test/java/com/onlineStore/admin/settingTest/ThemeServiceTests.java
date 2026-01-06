package com.onlineStore.admin.settingTest;

import com.onlineStore.admin.setting.country.SettingRepository;
import com.onlineStoreCom.entity.setting.Setting;
import com.onlineStoreCom.entity.setting.SettingCategory;
import com.onlineStoreCom.tenant.TenantContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(true)
public class ThemeServiceTests {

    @Autowired
    private SettingRepository repo;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testSaveThemeSettingsMultiTenant() {
        System.out.println("DEBUG: Starting testSaveThemeSettingsMultiTenant");
        // 1. Simulate Tenant 1
        TenantContext.setTenantId(1L);
        enableFilter(1L);

        // Setup initial list as if retrieved (empty or existing)
        List<Setting> tenant1List = new ArrayList<>(repo.findByCategory(SettingCategory.THEME));
        System.out.println("DEBUG: Initial tenant1List size: " + tenant1List.size());

        // Simulate "updateSettingValue" logic from ThemeController
        updateOrAdd(tenant1List, "THEME_COLOR_PRIMARY", "#FF0000", 1L);
        updateOrAdd(tenant1List, "THEME_HEADER_HEIGHT", "60px", 1L);

        System.out.println("DEBUG: tenant1List size before save: " + tenant1List.size());

        // Save
        repo.saveAll(tenant1List);
        System.out.println("DEBUG: Called saveAll");

        // Force flush/clear to test persistence
        entityManager.flush();
        entityManager.clear();
        enableFilter(1L);

        long count = repo.count();
        System.out.println("DEBUG: Total settings in DB (filtered): " + count);

        // Read back
        List<Setting> saved1 = repo.findByCategory(SettingCategory.THEME);
        System.out.println("DEBUG: saved1 size: " + saved1.size());

        assertThat(saved1).extracting(Setting::getValue).contains("#FF0000", "60px");
        assertThat(saved1).extracting(Setting::getKey).contains("THEME_COLOR_PRIMARY", "THEME_HEADER_HEIGHT");

        // 2. Simulate Tenant 2 (Isolation)
        TenantContext.setTenantId(2L);
        enableFilter(2L);

        List<Setting> tenant2List = new ArrayList<>(repo.findByCategory(SettingCategory.THEME));
        System.out.println("DEBUG: tenant2List size initial: " + tenant2List.size());
        assertThat(tenant2List).isEmpty();

        updateOrAdd(tenant2List, "THEME_COLOR_PRIMARY", "#00FF00", 2L);
        repo.saveAll(tenant2List);
        entityManager.flush();

        List<Setting> saved2 = repo.findByCategory(SettingCategory.THEME);
        System.out.println("DEBUG: saved2 size: " + saved2.size());
        assertThat(saved2).hasSize(1);
        assertThat(saved2.get(0).getValue()).isEqualTo("#00FF00");

        // 3. Verify Tenant 1 is untouched
        TenantContext.setTenantId(1L);
        enableFilter(1L);
        List<Setting> tenant1Again = repo.findByCategory(SettingCategory.THEME);
        Setting s = tenant1Again.stream().filter(x -> x.getKey().equals("THEME_COLOR_PRIMARY")).findFirst()
                .orElseThrow();
        assertThat(s.getValue()).isEqualTo("#FF0000");
    }

    @Test
    public void testUpdateExistingSetting() {
        TenantContext.setTenantId(1L);
        enableFilter(1L);

        // Create initial
        Setting initial = new Setting("THEME_FONT_SIZE", "14px", SettingCategory.THEME);
        initial.setTenantId(1L);
        repo.save(initial);
        entityManager.flush();
        entityManager.clear();
        enableFilter(1L);

        // Retrieve and Update
        List<Setting> list = new ArrayList<>(repo.findByCategory(SettingCategory.THEME));
        updateOrAdd(list, "THEME_FONT_SIZE", "18px", 1L);
        repo.saveAll(list);
        entityManager.flush();
        entityManager.clear();
        enableFilter(1L);

        // Verify
        Setting updated = repo.findByCategory(SettingCategory.THEME).get(0);
        assertThat(updated.getValue()).isEqualTo("18px");
        assertThat(repo.count()).isEqualTo(1);
    }

    private void enableFilter(Long tenantId) {
        org.hibernate.Session session = entityManager.getEntityManager().unwrap(org.hibernate.Session.class);
        session.enableFilter("tenantFilter").setParameter("tenantId", tenantId);
    }

    private void updateOrAdd(List<Setting> list, String key, String value, Long tenantId) {
        Setting setting = null;
        for (Setting s : list) {
            if (s.getKey().equals(key)) {
                setting = s;
                break;
            }
        }

        if (setting != null) {
            setting.setValue(value);
        } else {
            Setting settingNew = new Setting(key, value, SettingCategory.THEME);
            settingNew.setTenantId(tenantId);
            list.add(settingNew);
        }
    }
}
