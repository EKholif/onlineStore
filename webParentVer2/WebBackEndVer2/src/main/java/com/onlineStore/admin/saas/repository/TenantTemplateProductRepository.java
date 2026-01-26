package com.onlineStore.admin.saas.repository;

import com.onlineStoreCom.entity.saas.BusinessTemplate;
import com.onlineStoreCom.entity.saas.TenantTemplateProduct;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TenantTemplateProductRepository extends CrudRepository<TenantTemplateProduct, Integer> {
    List<TenantTemplateProduct> findByTemplate(BusinessTemplate template);
}
