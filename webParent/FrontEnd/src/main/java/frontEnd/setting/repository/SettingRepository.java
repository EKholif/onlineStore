package frontEnd.setting.repository;

import com.onlineStoreCom.entity.setting.Setting;
import com.onlineStoreCom.entity.setting.SettingCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SettingRepository extends JpaRepository<Setting, String> {
    // [AG-TEN-SEC-009] Secure Queries using SpEL to enforce Tenant Isolation at
    // Query Level
    @Query("SELECT s FROM Setting s WHERE s.category = ?1 AND (s.tenantId = :#{T(com.onlineStoreCom.tenant.TenantContext).getTenantId()} OR s.tenantId = 0 OR s.tenantId IS NULL)")
    List<Setting> findByCategory(SettingCategory category);

    @Query("SELECT s FROM Setting s WHERE (s.category = ?1 OR s.category = ?2) AND (s.tenantId = :#{T(com.onlineStoreCom.tenant.TenantContext).getTenantId()} OR s.tenantId = 0 OR s.tenantId IS NULL) ORDER BY s.tenantId ASC")
    List<Setting> findByTwoCategories(SettingCategory catOne, SettingCategory catTwo);

    Setting findByKey(String key);

    @Query("SELECT s FROM Setting s WHERE (s.tenantId = :#{T(com.onlineStoreCom.tenant.TenantContext).getTenantId()} OR s.tenantId = 0 OR s.tenantId IS NULL)")
    List<Setting> findAllSettings();
}
