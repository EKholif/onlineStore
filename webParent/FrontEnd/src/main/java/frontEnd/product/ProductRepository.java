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

        @Query(value = "SELECT * FROM products WHERE alias = ?1", nativeQuery = true)
        Product findByAlias(String alias);

        @Query(value = "SELECT p.* FROM products p JOIN categories c ON p.category_id = c.id "
                        + "WHERE p.enabled = 1 AND c.enabled = 1 AND c.id = ?1", nativeQuery = true)
        Set<Product> setProductByCategory(Integer categoryId);

        @Query(value = "SELECT p.* FROM products p JOIN categories c ON p.category_id = c.id "
                        + "WHERE c.enabled = 1 AND c.alias LIKE CONCAT('%', :keyword, '%') "
                + "ORDER BY p.name ASC", countQuery = "SELECT count(p.id) FROM products p JOIN categories c ON p.category_id = c.id WHERE c.enabled = 1 AND c.alias LIKE CONCAT('%', :keyword, '%')", nativeQuery = true)
        Page<Product> findAll(@Param("keyword") String keyword, Pageable pageable);

    @Query(value = "SELECT * FROM products WHERE enabled = 1 AND discount_percent > 0.0 ORDER BY name ASC", countQuery = "SELECT count(id) FROM products WHERE enabled = 1 AND discount_percent > 0.0", nativeQuery = true)
        Page<Product> pageProductOnSale(Pageable pageable);

        @Query(value = "SELECT * FROM products WHERE enabled = 1 AND discount_percent > 0 ORDER BY name ASC", nativeQuery = true)
        List<Product> findAllOnSale();

        @Query(value = "SELECT p.* FROM products p JOIN categories c ON p.category_id = c.id "
                        + "WHERE c.enabled = 1 AND c.alias LIKE CONCAT('%', ?1, '%') "
                + "ORDER BY p.name ASC", countQuery = "SELECT count(p.id) FROM products p JOIN categories c ON p.category_id = c.id WHERE c.enabled = 1 AND c.alias LIKE CONCAT('%', ?1, '%')", nativeQuery = true)
        Page<Product> findAllByProduct(String keyword, Pageable pageable);

        @Query(value = "SELECT p.* FROM products p JOIN categories c ON p.category_id = c.id "
                + "WHERE p.enabled = 1 AND c.enabled = 1 AND c.alias LIKE CONCAT('%', :keyword, '%')", countQuery = "SELECT count(p.id) FROM products p JOIN categories c ON p.category_id = c.id WHERE p.enabled = 1 AND c.enabled = 1 AND c.alias LIKE CONCAT('%', :keyword, '%')", nativeQuery = true)
        Page<Product> search(@Param("keyword") String keyword, Pageable pageable);

        @Query(value = "SELECT p.* FROM products p WHERE p.enabled = 1 "
                + "AND CONCAT(p.id, ' ', p.name, ' ', p.alias) LIKE CONCAT('%', ?1, '%')", countQuery = "SELECT count(p.id) FROM products p WHERE p.enabled = 1 AND CONCAT(p.id, ' ', p.name, ' ', p.alias) LIKE CONCAT('%', ?1, '%')", nativeQuery = true)
        Page<Product> findAllBYPage(String keyword, Pageable pageable);

        @Modifying
        @Transactional
        @Query(value = "UPDATE products p SET p.average_rating = COALESCE((SELECT AVG(r.rating) FROM reviews r WHERE r.product_id = ?1), 0),"
                        + " p.review_count = (SELECT COUNT(r.id) FROM reviews r WHERE r.product_id = ?1) "
                        + "WHERE p.id = ?1 AND p.tenant_id = :#{T(com.onlineStoreCom.tenant.TenantContext).getTenantId()}", nativeQuery = true)
        void updateReviewCountAndAverageRating(Integer productId);

}