package com.onlineStore.admin.brand;


import com.onlineStoreCom.entity.brand.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {

    public Long countById(Long id);

    public Brand findByName(String name);

}
