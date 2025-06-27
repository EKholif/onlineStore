package com.onlineStore.admin.brand.reposetry;


import com.onlineStoreCom.entity.brand.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {

    Long countById(Long id);

    Brand findByName(String name);


    @Query("SELECT b FROM Brand b WHERE  CONCAT(b.id, ' ', b.name, ' ', b.logo ) LIKE %?1%")
    Page<Brand> findAll(String keyword, Pageable pageable);

    @Query("SELECT new Brand(b.id,b.name) FROM Brand b ORDER BY  b.name ASC")
    List<Brand> findAll();
}
