package com.onlineStore.admin.feature;

import com.onlineStoreCom.entity.FeatureFlag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FeatureFlagService {

    @Autowired
    private FeatureFlagRepository featureFlagRepository;

    public List<FeatureFlag> listGlobalFlags() {
        return featureFlagRepository.findByTenantIdIsNull();
    }

    public void save(FeatureFlag featureFlag) {
        featureFlagRepository.save(featureFlag);
    }

    public FeatureFlag get(Long id) {
        return featureFlagRepository.findById(id).orElse(null);
    }

    public void delete(Long id) {
        featureFlagRepository.deleteById(id);
    }

    public boolean isFeatureEnabled(String key, String tenantId) {
        // Check tenant specific
        if (tenantId != null) {
            Optional<FeatureFlag> tenantFlag = featureFlagRepository.findByKeyAndTenantId(key, tenantId);
            if (tenantFlag.isPresent()) {
                return tenantFlag.get().isEnabled();
            }
        }
        // Fallback to global
        Optional<FeatureFlag> globalFlag = featureFlagRepository.findByKeyAndTenantId(key, null);
        return globalFlag.map(FeatureFlag::isEnabled).orElse(false); // Default disabled if not found
    }
}
