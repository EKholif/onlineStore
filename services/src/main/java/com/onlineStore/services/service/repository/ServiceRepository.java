package com.onlineStore.services.service.repository;

import com.onlineStoreCom.entity.service.Service;
import com.onlineStore.services.common.repository.SearchRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceRepository extends SearchRepository<Service, Integer> {

    @Query("SELECT s FROM Service s WHERE s.name LIKE %?1% OR s.shortDescription LIKE %?1%")
    Page<Service> findAll(String keyword, Pageable pageable);

    Long countById(Integer id);

    Service findByName(String name);

    Service findByAlias(String alias);

    @Query("UPDATE Service s SET s.enabled = ?2 WHERE s.id = ?1")
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.transaction.annotation.Transactional
    void updateEnabledStatus(Integer id, boolean enabled);
}
