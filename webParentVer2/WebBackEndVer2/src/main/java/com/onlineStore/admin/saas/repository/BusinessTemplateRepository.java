package com.onlineStore.admin.saas.repository;

import com.onlineStoreCom.entity.saas.BusinessDomain;
import com.onlineStoreCom.entity.saas.BusinessTemplate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusinessTemplateRepository extends CrudRepository<BusinessTemplate, Integer> {
    List<BusinessTemplate> findByBusinessDomain(BusinessDomain businessDomain);
    BusinessTemplate findByName(String name);
}
