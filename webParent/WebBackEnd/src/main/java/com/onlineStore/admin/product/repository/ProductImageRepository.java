package com.onlineStore.admin.product.repository;

import com.onlineStoreCom.entity.product.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Integer> {


}
