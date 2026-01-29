package com.onlineStoreCom.security.tenant;

import com.onlineStoreCom.exception.TenantNotFoundException;
import com.onlineStoreCom.tenant.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TenantContextFilterTest {

    @Mock
    private TenantResolutionService tenantResolutionService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain chain;

    private TenantContextFilter filter;

    @BeforeEach
    void setUp() {
        filter = new TenantContextFilter(tenantResolutionService);
        TenantContext.clear();
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    void doFilter_ValidSubdomain_ShouldSetContext() throws Exception {
        // Given
        when(request.getServerName()).thenReturn("tenant1.saas.com");
        when(tenantResolutionService.resolveTenantId("tenant1")).thenReturn(101L);

        // Verify inside chain because context is cleared in finally block
        doAnswer(invocation -> {
            assertEquals(101L, TenantContext.getTenantId());
            return null;
        }).when(chain).doFilter(request, response);

        // When
        filter.doFilter(request, response, chain);

        // Then
        verify(chain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilter_PlatformHost_ShouldSetContextToZero() throws Exception {
        // Given
        when(request.getServerName()).thenReturn("localhost");

        doAnswer(invocation -> {
            assertEquals(0L, TenantContext.getTenantId());
            return null;
        }).when(chain).doFilter(request, response);

        // When
        filter.doFilter(request, response, chain);

        // Then
        verify(tenantResolutionService, never()).resolveTenantId(anyString());
        verify(chain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilter_UnknownTenant_ShouldFailClosed() throws Exception {
        // Given
        // request.getRequestURI() is NOT used in the catch block path, so remove stub
        // to avoid UnnecessaryStubbingException
        // when(request.getRequestURI()).thenReturn("/dashboard");

        when(request.getServerName()).thenReturn("unknown.saas.com");
        when(tenantResolutionService.resolveTenantId("unknown")).thenThrow(new TenantNotFoundException("Not Found"));

        // When
        filter.doFilter(request, response, chain);

        // Then
        assertNull(TenantContext.getTenantId()); // Should be cleared
        verify(chain, never()).doFilter(request, response); // Chain blocked
        verify(response, times(1)).sendError(eq(HttpServletResponse.SC_NOT_FOUND), anyString());
    }

    @Test
    void doFilter_NoSubdomain_ShouldFailClosed() throws Exception {
        // Given
        when(request.getRequestURI()).thenReturn("/api"); // Used in logging inside Fail Closed block
        when(request.getServerName()).thenReturn("192.168.1.1"); // IP Address

        // When
        filter.doFilter(request, response, chain);

        // Then
        assertNull(TenantContext.getTenantId());
        verify(chain, never()).doFilter(request, response);
        verify(response, times(1)).sendError(eq(HttpServletResponse.SC_NOT_FOUND), anyString());
    }
}
