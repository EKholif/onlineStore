package com.onlineStore.admin.saas.repository;

import com.onlineStoreCom.entity.saas.BusinessDomain;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusinessDomainRepository extends CrudRepository<BusinessDomain, Integer> {
    BusinessDomain findByName(String name);
    List<BusinessDomain> findAll();
}
