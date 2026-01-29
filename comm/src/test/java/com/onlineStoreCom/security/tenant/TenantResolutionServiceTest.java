package com.onlineStoreCom.security.tenant;

import com.onlineStoreCom.entity.tenant.Tenant;
import com.onlineStoreCom.exception.TenantNotFoundException;
import com.onlineStoreCom.repo.TenantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TenantResolutionServiceTest {

    @Mock
    private TenantRepository tenantRepository;

    private TenantResolutionService tenantResolutionService;

    @BeforeEach
    void setUp() {
        tenantResolutionService = new TenantResolutionService(tenantRepository);
    }

    @Test
    void resolveTenantId_CacheMiss_FoundInDB_ShouldCacheAndReturn() {
        // Given
        String tenantKey = "tenant1";
        Long expectedId = 100L;
        Tenant tenant = new Tenant();
        tenant.setId(expectedId);
        tenant.setCode(tenantKey);

        when(tenantRepository.findByCode(tenantKey)).thenReturn(Optional.of(tenant));

        // When
        Long resultId = tenantResolutionService.resolveTenantId(tenantKey);

        // Then
        assertEquals(expectedId, resultId);
        verify(tenantRepository, times(1)).findByCode(tenantKey);
    }

    @Test
    void resolveTenantId_CacheHit_ShouldReturnCachedValueWithoutDBCall() {
        // Given
        String tenantKey = "tenant1";
        Long expectedId = 100L;
        Tenant tenant = new Tenant();
        tenant.setId(expectedId);

        // Prime the cache
        when(tenantRepository.findByCode(tenantKey)).thenReturn(Optional.of(tenant));
        tenantResolutionService.resolveTenantId(tenantKey); // First call to cache it
        verify(tenantRepository, times(1)).findByCode(tenantKey);

        // Reset invocations to prove second call doesn't touch DB
        clearInvocations(tenantRepository);

        // When
        Long resultId = tenantResolutionService.resolveTenantId(tenantKey);

        // Then
        assertEquals(expectedId, resultId);
        verify(tenantRepository, never()).findByCode(anyString());
    }

    @Test
    void resolveTenantId_NotFoundInDB_ShouldThrowException() {
        // Given
        String tenantKey = "invalid";
        when(tenantRepository.findByCode(tenantKey)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(TenantNotFoundException.class, () -> {
            tenantResolutionService.resolveTenantId(tenantKey);
        });
        verify(tenantRepository, times(1)).findByCode(tenantKey);
    }

    @Test
    void resolveTenantId_NullOrEmpty_ShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> tenantResolutionService.resolveTenantId(null));
        assertThrows(IllegalArgumentException.class, () -> tenantResolutionService.resolveTenantId(""));
    }
}
