package com.onlineStore.admin.brand.reposetry;

import com.onlineStoreCom.entity.brand.Brand;
import com.onlineStoreCom.repo.base.BaseTenantRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrandRepository extends BaseTenantRepository<Brand, Integer> {

    @Query("SELECT count(b) FROM Brand b WHERE b.id = ?1 AND b.tenantId = :#{T(com.onlineStoreCom.tenant.TenantContext).getTenantId()}")
    Integer countById(Integer id);

    @Query("SELECT b FROM Brand b WHERE b.name = ?1 AND b.tenantId = :#{T(com.onlineStoreCom.tenant.TenantContext).getTenantId()}")
    Brand findByName(String name);

    @Query("SELECT b FROM Brand b WHERE CONCAT(b.id, ' ', b.name, ' ', b.logo ) LIKE %?1% AND b.tenantId = :#{T(com.onlineStoreCom.tenant.TenantContext).getTenantId()}")
    Page<Brand> findAll(String keyword, Pageable pageable);

    @Query("SELECT b FROM Brand b JOIN b.categories c WHERE c.id = ?2 AND CONCAT(b.id, ' ', b.name, ' ', b.logo ) LIKE %?1% AND b.tenantId = :#{T(com.onlineStoreCom.tenant.TenantContext).getTenantId()}")
    Page<Brand> findAll(String keyword, Integer categoryId, Pageable pageable);

    @Query("SELECT new Brand(b.id,b.name) FROM Brand b WHERE b.tenantId = :#{T(com.onlineStoreCom.tenant.TenantContext).getTenantId()} ORDER BY b.name ASC")
    List<Brand> findAll();
}
