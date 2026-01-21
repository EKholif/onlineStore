package com.onlineStore.admin.feature;

import com.onlineStoreCom.entity.FeatureFlag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeatureFlagRepository extends JpaRepository<FeatureFlag, Long> {

    List<FeatureFlag> findByTenantId(String tenantId);

    Optional<FeatureFlag> findByKeyAndTenantId(String key, String tenantId);

    List<FeatureFlag> findByTenantIdIsNull();
}
