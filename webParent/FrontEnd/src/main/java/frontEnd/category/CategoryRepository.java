package frontEnd.category;

import com.onlineStoreCom.entity.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

        // [AG-TEN-FIX-002] Removed parent.enabled check to prevent cross-tenant JOIN
        // leak
        @Query("SELECT c FROM Category c WHERE c.tenantId = :#{T(com.onlineStoreCom.tenant.TenantContext).getTenantId()} AND c.enabled = true ORDER BY c.name ASC")
        List<Category> findAllEnabled();

        // [AG-TEN-FIX-002] Removed parent.enabled check to prevent cross-tenant JOIN
        // leak
        // [AG-TEN-FIX-002] Removed parent.enabled check to prevent cross-tenant JOIN
        // leak
        // Fixed "Cannot resolve symbol 'id'" by comparing Entity directly
        @Query("SELECT c FROM Category c WHERE c.tenantId = :#{T(com.onlineStoreCom.tenant.TenantContext).getTenantId()} AND c.enabled = true AND c.parent = ?1")
        Set<Category> getChildren(Category parent);

        // [AG-TEN-FIX-002] Removed parent.enabled check to prevent cross-tenant JOIN
        // leak
        @Query("SELECT c.parent FROM Category c WHERE c.tenantId = :#{T(com.onlineStoreCom.tenant.TenantContext).getTenantId()} AND c.enabled = true AND c.id = ?1")
        Category getParent(Integer categoryId);

        @Query("SELECT c FROM Category c WHERE c.tenantId = :#{T(com.onlineStoreCom.tenant.TenantContext).getTenantId()} AND c.enabled = true AND c.parent IS NULL")
        List<Category> notParentCategory();

        // [AG-TEN-FIX-002] Removed parent.enabled check to prevent cross-tenant JOIN
        // leak
        @Query("SELECT c FROM Category c WHERE c.tenantId = :#{T(com.onlineStoreCom.tenant.TenantContext).getTenantId()} AND c.enabled = true AND c.alias = ?1")
        Category findByAliasEnabled(String alias);

        // [AG-TEN-FIX-002] Removed parent.enabled check to prevent cross-tenant JOIN
        // leak
        @Query("SELECT c FROM Category c WHERE c.tenantId = :#{T(com.onlineStoreCom.tenant.TenantContext).getTenantId()} AND c.enabled = true AND c.name = ?1")
        Category findByNameEnabled(String name);

        @Query("SELECT c FROM Category c WHERE c.tenantId = :#{T(com.onlineStoreCom.tenant.TenantContext).getTenantId()} AND c.parent IS NULL AND c.enabled = true AND c.alias = ?1")
        Category findByAliasParent(String alias);

}
