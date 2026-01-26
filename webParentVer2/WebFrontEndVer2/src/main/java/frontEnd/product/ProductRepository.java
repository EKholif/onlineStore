package frontEnd.product;

import com.onlineStoreCom.entity.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

        @Query("SELECT p FROM Product p WHERE p.alias = ?1")
        Product findByAlias(String alias);

        @Query("SELECT p FROM Product p WHERE p.enabled = true AND p.category.enabled = true AND p.category.id = ?1")
        Set<Product> setProductByCategory(Integer categoryId);

        @Query("SELECT p FROM Product p WHERE p.category.enabled = true AND p.category.alias LIKE %:keyword% ORDER BY p.name ASC")
        Page<Product> findAll(@Param("keyword") String keyword, Pageable pageable);

        @Query("SELECT p FROM Product p WHERE p.enabled = true AND p.discountPercent > 0.0 ORDER BY p.name ASC")
        Page<Product> pageProductOnSale(Pageable pageable);

        @Query("SELECT p FROM Product p WHERE p.tenantId = :#{T(com.onlineStoreCom.tenant.TenantContext).getTenantId()} AND p.enabled = true AND p.discountPercent > 0 ORDER BY p.name ASC")
        List<Product> findAllOnSale();

        @Query("SELECT p FROM Product p WHERE p.category.enabled = true AND p.category.alias LIKE %?1% ORDER BY p.name ASC")
        Page<Product> findAllByProduct(String keyword, Pageable pageable);

        @Query("SELECT p FROM Product p WHERE p.enabled = true AND p.category.enabled = true AND p.category.alias LIKE %:keyword%")
        Page<Product> search(@Param("keyword") String keyword, Pageable pageable);

        @Query("SELECT p FROM Product p WHERE p.enabled = true AND (CONCAT(p.id, ' ', p.name, ' ', p.alias) LIKE %?1%)")
        Page<Product> findAllBYPage(String keyword, Pageable pageable);

        @Modifying
        @Transactional
        @Query("UPDATE Product p SET p.averageRating = CAST(COALESCE((SELECT AVG(r.rating) FROM Review r WHERE r.product.id = ?1), 0.0) AS float), "
                + "p.reviewCount = (SELECT COUNT(r.id) FROM Review r WHERE r.product.id = ?1) "
                + "WHERE p.id = ?1")
        void updateReviewCountAndAverageRating(Integer productId);

}