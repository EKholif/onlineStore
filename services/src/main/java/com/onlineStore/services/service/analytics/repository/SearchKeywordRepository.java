package com.onlineStore.services.service.analytics.repository;

import com.onlineStoreCom.entity.analytics.SearchKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SearchKeywordRepository extends JpaRepository<SearchKeyword, Long> {
    Optional<SearchKeyword> findByTenantIdAndKeyword(Integer tenantId, String keyword);

    java.util.List<SearchKeyword> findByTenantIdOrderByCountDesc(Integer tenantId, org.springframework.data.domain.Pageable pageable);

    @org.springframework.data.jpa.repository.Query("SELECT new com.onlineStoreCom.entity.analytics.SearchKeyword(MAX(s.tenantId), s.keyword, SUM(s.count)) FROM SearchKeyword s GROUP BY s.keyword ORDER BY SUM(s.count) DESC")
    java.util.List<SearchKeyword> findPlatformTopKeywords(org.springframework.data.domain.Pageable pageable);
}
