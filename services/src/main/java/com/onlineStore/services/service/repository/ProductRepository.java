package com.onlineStore.services.service.repository;

import com.onlineStoreCom.entity.product.Product;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends com.onlineStoreCom.repo.base.BaseTenantRepository<Product, Integer> {

    @Query("SELECT p FROM Product p WHERE p.name = ?1 AND p.tenantId = :#{T(com.onlineStoreCom.tenant.TenantContext).getTenantId()}")
    Product findByName(String name);

    @Query("SELECT p FROM Product p WHERE p.alias = ?1 AND p.tenantId = :#{T(com.onlineStoreCom.tenant.TenantContext).getTenantId()}")
    Product findByAlias(String alias);

    @Query("SELECT p FROM Product p WHERE (CONCAT(p.id, ' ', p.name, ' ', p.alias) LIKE %?1%) AND p.tenantId = :#{T(com.onlineStoreCom.tenant.TenantContext).getTenantId()}")
    Page<Product> findAll(String keyword, Pageable pageable);

    @Query("UPDATE Product p set p.enabled=?2 WHERE p.id = ?1 AND p.tenantId = :#{T(com.onlineStoreCom.tenant.TenantContext).getTenantId()}")
    @Modifying
    Integer enableProduct(Integer id, boolean enable);

    // [AG-TEN-RISK-001] Restricted to Current Tenant
    @Query("UPDATE Product p SET p.enabled = true WHERE p.tenantId = :#{T(com.onlineStoreCom.tenant.TenantContext).getTenantId()}")
    @Modifying
    @Transactional
    void enableProductAll();

    @Query("SELECT p FROM Product p WHERE p.name LIKE %?1% AND p.tenantId = :#{T(com.onlineStoreCom.tenant.TenantContext).getTenantId()}")
    Page<Product> searchProductsByName(String keyword, Pageable pageable);

    @Modifying
    @Transactional
    @Query("""
                UPDATE Product p
                SET p.averageRating = COALESCE(
                    CAST((SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId) AS float),
                    0
                ),
                p.reviewCount = (
                    SELECT COUNT(r.id) FROM Review r WHERE r.product.id = :productId
                )
                WHERE p.id = :productId AND p.tenantId = :#{T(com.onlineStoreCom.tenant.TenantContext).getTenantId()}
            """)
    void updateReviewCountAndAverageRating(@Param("productId") Integer productId);

    @Query("SELECT p FROM Product p WHERE (p.tenantId = :tenantId OR p.tenantId = 0) AND p.id NOT IN (SELECT s.productId FROM DailyProductStats s WHERE s.tenantId = :tenantId)")
    Page<Product> findProductsWithZeroViews(@Param("tenantId") Integer tenantId, Pageable pageable);

}
