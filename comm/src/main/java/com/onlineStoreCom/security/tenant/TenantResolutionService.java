package com.onlineStoreCom.security.tenant;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.onlineStoreCom.entity.tenant.Tenant;
import com.onlineStoreCom.exception.TenantNotFoundException;
import com.onlineStoreCom.repo.TenantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Service responsible for resolving Tenant ID from a lookup key
 * (subdomain/code).
 * Includes Caching (Caffeine) to minimize DB hits.
 */
@Service
public class TenantResolutionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TenantResolutionService.class);

    private final TenantRepository tenantRepository;
    private final Cache<String, Long> tenantCache;

    @Autowired
    public TenantResolutionService(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
        this.tenantCache = Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.HOURS)
                .maximumSize(1000)
                .build();
    }

    /**
     * Resolves the Tenant ID for a given tenant key (subdomain/code).
     *
     * @param tenantKey The unique code/subdomain of the tenant.
     * @return The Tenant ID.
     * @throws TenantNotFoundException if the tenant does not exist.
     */
    public Long resolveTenantId(String tenantKey) {
        if (tenantKey == null || tenantKey.isBlank()) {
            throw new IllegalArgumentException("Tenant Key cannot be null or empty");
        }

        // 1. Check Cache
        Long cachedId = tenantCache.getIfPresent(tenantKey);
        if (cachedId != null) {
            LOGGER.debug("Tenant Resolution Cache HIT: {} -> {}", tenantKey, cachedId);
            return cachedId;
        }

        LOGGER.debug("Tenant Resolution Cache MISS: {}. Querying DB...", tenantKey);

        // 2. Query DB
        Optional<Tenant> tenantOpt = tenantRepository.findByCode(tenantKey);

        if (tenantOpt.isPresent()) {
            Long id = tenantOpt.get().getId();
            // 3. Update Cache
            tenantCache.put(tenantKey, id);
            LOGGER.debug("Tenant Resolved from DB: {} -> {}", tenantKey, id);
            return id;
        } else {
            LOGGER.warn("Tenant Not Found for key: {}", tenantKey);
            throw new TenantNotFoundException("Tenant not found for key: " + tenantKey);
        }
    }

    /**
     * Invalidate cache for a specific key (useful for admin updates).
     */
    public void invalidate(String tenantKey) {
        tenantCache.invalidate(tenantKey);
    }
}
