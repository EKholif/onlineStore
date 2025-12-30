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

    Product findByAlias(String alias);

    @Query("SELECT p FROM Product p WHERE p.enable = true "
            + "AND p.category.enable= true " + "AND (p.category.id = ?1 )")
    Set<Product> setProductByCategory(Integer categoryId);

    @Query("SELECT p FROM Product p WHERE p.category.enable = true AND p.category.alias LIKE CONCAT('%', :keyword, '%') ORDER BY p.name ASC")
    Page<Product> findAll(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.enable = true AND p.discountPercent > 0.0 ORDER BY p.name ASC")
    Page<Product> pageProductOnSale(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.enable = true AND p.discountPercent > 0 ORDER BY p.name ASC")
    List<Product> findAllOnSale();

    @Query("SELECT p FROM Product p WHERE p.category.enable = true AND p.category.alias LIKE %?1% ORDER BY p.name ASC")
    Page<Product> findAllByProduct(String keyword, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.enable = true AND p.category.enable = true AND p.category.alias LIKE CONCAT('%', :keyword, '%')")
    Page<Product> search(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.enable = true AND CONCAT(p.id, ' ', p.name, ' ', p.alias) LIKE %?1%")
    Page<Product> findAllBYPage(String keyword, Pageable pageable);

    // @Query(
    // value = "SELECT * FROM shop.products WHERE enabled = true AND " +
    // "MATCH(name, short_description, full_description) AGAINST (?1)",
    // nativeQuery = true
    // )
    // Page<Product> search(String keyword, Pageable pageable);

    @Modifying
    @Transactional
    @Query(value = "UPDATE products p SET p.average_rating = COALESCE((SELECT AVG(r.rating) FROM reviews r WHERE r.product_id = ?1), 0),"
            + " p.review_count = (SELECT COUNT(r.id) FROM reviews r WHERE r.product_id = ?1) WHERE p.id = ?1", nativeQuery = true)
    void updateReviewCountAndAverageRating(Integer productId);

}