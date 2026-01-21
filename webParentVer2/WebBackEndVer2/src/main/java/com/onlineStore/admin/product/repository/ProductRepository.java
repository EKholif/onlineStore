package com.onlineStore.admin.product.repository;

import com.onlineStoreCom.entity.product.Product;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    @Query("SELECT p FROM Product p WHERE p.name = ?1")
    Product findByName(String name);

    @Query("SELECT p FROM Product p WHERE p.alias = ?1")
    Product findByAlias(String alias);

    @Query("SELECT p FROM Product p WHERE (CONCAT(p.id, ' ', p.name, ' ', p.alias) LIKE %?1%)")
    Page<Product> findAll(String keyword, Pageable pageable);

    @Query("UPDATE Product p set p.enabled=?2 WHERE p.id = ?1")
    @Modifying
    Integer enableProduct(Integer id, boolean enable);

    // [AG-TEN-RISK-001] Native Query must still be careful, but user rejected
    // manual WHERE in standard queries.
    // Keeping this one safe as it is NATIVE SQL and Hibernate Filter doesn't apply
    // to Native SQL automatically without specific config.
    // However, user said "filter not passing", implying they want global
    // protection.
    // I will revert standard JPQL queries first.

    // [AG-TEN-RISK-001] Refactored to JPQL to utilize Hibernate Tenant Filter
    @Query("UPDATE Product p SET p.enabled = true")
    @Modifying
    @Transactional
    void enableProductAll();

    @Query("SELECT p FROM Product p WHERE p.name LIKE %?1%")
    public Page<Product> searchProductsByName(String keyword, Pageable pageable);

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
                WHERE p.id = :productId
            """)
    void updateReviewCountAndAverageRating(@Param("productId") Integer productId);

}
