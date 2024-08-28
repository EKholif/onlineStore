package com.onlineStore.admin.product.repository;

import com.onlineStoreCom.entity.product.ProductDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProductDetailsRepository extends JpaRepository<ProductDetails, Integer> {


}
