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

    Product findByName(String name);

    Product findByAlias(String alias);


    @Query("SELECT p FROM Product p WHERE  CONCAT(p.id, ' ', p.name, ' ', p.alias ) LIKE %?1%")
    Page<Product> findAll(String keyword, Pageable pageable);


    @Query("UPDATE Product p set  p.enable=?2 WHERE p.id = ?1 ")
    @Modifying
    Integer enableProduct(Integer id, boolean enable);

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


