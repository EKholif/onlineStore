package com.example.frontend.product;

import com.onlineStoreCom.entity.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;


@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

//    @Query("SELECT p FROM Product p WHERE p.enable = true "
//            + "AND (p.category.id = ?1 )"
//            + " ORDER BY p.name ASC")
//    public Page<Product> listByCategory(Integer categoryId, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.enable = true "
            + "AND (p.category.id = ?1 )")
    public Set<Product> setProductByCategory(Long categoryId);

//    @Query("SELECT p FROM Product p WHERE p.enable = true "
//            + "AND (p.brand.id = ?1 )"
//            + " ORDER BY p.name ASC")
//    public Set<Product> SetByBrand(Integer brandId, String categoryIDMatch, Pageable pageable);








//    @Query(value = "SELECT * FROM products WHERE enabled = true AND "
//            + "MATCH(name, short_description, full_description) AGAINST (?1)",
//            nativeQuery = true)
//    public Page<Product> search(String keyword, Pageable pageable);

//    @Query("Update Product p SET p.averageRating = COALESCE((SELECT AVG(r.rating) FROM Review r WHERE r.product.id = ?1), 0),"
//            + " p.reviewCount = (SELECT COUNT(r.id) FROM Review r WHERE r.product.id =?1) "
//            + "WHERE p.id = ?1")
//    @Modifying
//    public void updateReviewCountAndAverageRating(Integer productId);
}
