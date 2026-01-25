package com.onlineStore.admin.security;

import com.onlineStore.admin.audit.TenantAuditLogRepository;
import com.onlineStoreCom.entity.audit.TenantAuditLog;
import com.onlineStoreCom.entity.tenant.Tenant;
import com.onlineStoreCom.repo.TenantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * AG-TEST-SWITCH-001: Integration Tests for Tenant Switcher Controller
 * <p>
 * Purpose:
 * - Test REST API endpoints for tenant switching
 * - Verify authorization and access control
 * - Validate session management
 * - Verify audit log creation
 * <p>
 * Test Scenarios:
 * - GET /api/tenant-switcher/accessible-tenants → correct list per role
 * - POST /api/tenant-switcher/switch → authorized switch succeeds
 * - POST /api/tenant-switcher/switch → unauthorized returns 403
 * - POST /api/tenant-switcher/reset → session resets correctly
 * - Audit logs created for all operations
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Tenant Switcher Controller Integration Tests")
public class TenantSwitcherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TenantRepository tenantRepo;

    @Autowired
    private TenantAuditLogRepository auditLogRepo;

    private Tenant masterTenant;
    private Tenant agencyTenant;
    private Tenant childTenant;

    @BeforeEach
    void setUp() {
        // Create test tenant hierarchy
        masterTenant = createTenant(0L, "SaaS Master", "MASTER", null);
        agencyTenant = createTenant(1L, "Agency Tenant", "AGENCY", masterTenant);
        childTenant = createTenant(2L, "Child Tenant", "CHILD", agencyTenant);

        // Clear audit logs from previous tests
        auditLogRepo.deleteAll();
    }

    // ============================================================================
    // GET /api/tenant-switcher/accessible-tenants Tests
    // ============================================================================

    @Test
    @DisplayName("GET /accessible-tenants returns 401 for unauthenticated user")
    void getAccessibleTenants_Unauthenticated_Returns401() throws Exception {
        mockMvc.perform(get("/api/tenant-switcher/accessible-tenants"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "platform@admin.com", roles = {"ADMIN"})
    @DisplayName("GET /accessible-tenants returns all tenants for Platform Admin")
    void getAccessibleTenants_PlatformAdmin_ReturnsAll() throws Exception {
        mockMvc.perform(get("/api/tenant-switcher/accessible-tenants"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(3))))
                .andExpect(jsonPath("$[*].id", hasItem(0)))
                .andExpect(jsonPath("$[*].id", hasItem(1)))
                .andExpect(jsonPath("$[*].id", hasItem(2)))
                .andExpect(jsonPath("$[0].name", notNullValue()))
                .andExpect(jsonPath("$[0].code", notNullValue()));
    }

    @Test
    @WithMockUser(username = "agency@admin.com", roles = {"ADMIN"})
    @DisplayName("GET /accessible-tenants returns own + children for Agency Admin")
    void getAccessibleTenants_AgencyAdmin_ReturnsOwnAndChildren() throws Exception {
        mockMvc.perform(get("/api/tenant-switcher/accessible-tenants"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2))) // Agency + Child
                .andExpect(jsonPath("$[*].id", hasItem(1)))
                .andExpect(jsonPath("$[*].id", hasItem(2)))
                .andExpect(jsonPath("$[*].id", not(hasItem(0)))); // Should NOT include Master
    }

    @Test
    @WithMockUser(username = "tenant@admin.com", roles = {"ADMIN"})
    @DisplayName("GET /accessible-tenants returns only own tenant for Tenant Admin")
    void getAccessibleTenants_TenantAdmin_ReturnsOwnOnly() throws Exception {
        mockMvc.perform(get("/api/tenant-switcher/accessible-tenants"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(2)))
                .andExpect(jsonPath("$[0].name", is("Child Tenant")));
    }

    // ============================================================================
    // POST /api/tenant-switcher/switch Tests
    // ============================================================================

    @Test
    @WithMockUser(username = "platform@admin.com", roles = {"ADMIN"})
    @DisplayName("POST /switch - Platform Admin can switch to any tenant")
    void switchTenant_PlatformAdmin_ToAnyTenant_Success() throws Exception {
        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/api/tenant-switcher/switch")
                        .param("tenantId", "1")
                        .session(session))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is("true")))
                .andExpect(jsonPath("$.message", containsString("Switched to tenant")))
                .andExpect(jsonPath("$.tenantId", is("1")));

        // Verify session updated
        assertEquals(1L, session.getAttribute("SWITCHED_TENANT_ID"),
                "SWITCHED_TENANT_ID should be set in session");

        assertNotNull(session.getAttribute("ORIGINAL_TENANT_ID"),
                "ORIGINAL_TENANT_ID should be preserved");
    }

    @Test
    @WithMockUser(username = "agency@admin.com", roles = {"ADMIN"})
    @DisplayName("POST /switch - Agency Admin can switch to child tenant")
    void switchTenant_AgencyAdmin_ToChild_Success() throws Exception {
        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/api/tenant-switcher/switch")
                        .param("tenantId", "2") // Child Tenant
                        .session(session))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is("true")));

        assertEquals(2L, session.getAttribute("SWITCHED_TENANT_ID"));
    }

    @Test
    @WithMockUser(username = "agency@admin.com", roles = {"ADMIN"})
    @DisplayName("POST /switch - Agency Admin CANNOT switch to Master tenant")
    void switchTenant_AgencyAdmin_ToMaster_Returns403() throws Exception {
        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/api/tenant-switcher/switch")
                        .param("tenantId", "0") // Master Tenant (unauthorized)
                        .session(session))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error", is("Access denied")));

        // Session should NOT be updated
        assertNull(session.getAttribute("SWITCHED_TENANT_ID"),
                "SWITCHED_TENANT_ID should NOT be set for denied switch");
    }

    @Test
    @WithMockUser(username = "tenant@admin.com", roles = {"ADMIN"})
    @DisplayName("POST /switch - Tenant Admin CANNOT switch to other tenants")
    void switchTenant_TenantAdmin_ToOther_Returns403() throws Exception {
        mockMvc.perform(post("/api/tenant-switcher/switch")
                        .param("tenantId", "1")) // Agency Tenant (unauthorized)
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error", is("Access denied")));
    }

    @Test
    @WithMockUser(username = "platform@admin.com", roles = {"ADMIN"})
    @DisplayName("POST /switch - Invalid tenant ID returns 404")
    void switchTenant_InvalidTenantId_Returns404() throws Exception {
        mockMvc.perform(post("/api/tenant-switcher/switch")
                        .param("tenantId", "999")) // Non-existent tenant
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Tenant not found")));
    }

    // ============================================================================
    // POST /api/tenant-switcher/reset Tests
    // ============================================================================

    @Test
    @WithMockUser(username = "platform@admin.com", roles = {"ADMIN"})
    @DisplayName("POST /reset - Clears SWITCHED_TENANT_ID and restores original")
    void resetTenant_ClearsSessionAndRestoresOriginal() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("ORIGINAL_TENANT_ID", 0L);
        session.setAttribute("SWITCHED_TENANT_ID", 1L);

        mockMvc.perform(post("/api/tenant-switcher/reset")
                        .session(session))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is("true")))
                .andExpect(jsonPath("$.message", containsString("Returned to")));

        // Verify session cleared
        assertNull(session.getAttribute("SWITCHED_TENANT_ID"),
                "SWITCHED_TENANT_ID should be cleared");

        assertNull(session.getAttribute("ORIGINAL_TENANT_ID"),
                "ORIGINAL_TENANT_ID should be cleared");
    }

    @Test
    @DisplayName("POST /reset - Returns 401 for unauthenticated user")
    void resetTenant_Unauthenticated_Returns401() throws Exception {
        mockMvc.perform(post("/api/tenant-switcher/reset"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    // ============================================================================
    // Audit Log Verification Tests
    // ============================================================================

    @Test
    @WithMockUser(username = "platform@admin.com", roles = {"ADMIN"})
    @DisplayName("Successful switch creates audit log entry")
    void switchTenant_Success_CreatesAuditLog() throws Exception {
        long auditCountBefore = auditLogRepo.count();

        mockMvc.perform(post("/api/tenant-switcher/switch")
                        .param("tenantId", "1"))
                .andExpect(status().isOk());

        // Verify audit log created
        long auditCountAfter = auditLogRepo.count();
        assertEquals(auditCountBefore + 1, auditCountAfter,
                "Audit log should be created for successful switch");

        // Verify audit log details
        List<TenantAuditLog> logs = auditLogRepo.findAllByOrderByCreatedAtDesc(org.springframework.data.domain.Pageable.unpaged()).getContent();
        TenantAuditLog latestLog = logs.get(0);

        assertEquals("SWITCH_TENANT", latestLog.getAction(),
                "Action should be SWITCH_TENANT");

        assertNotNull(latestLog.getTargetTenant(),
                "Target tenant should be recorded");

        assertEquals(1L, latestLog.getTargetTenant().getId(),
                "Target tenant ID should be 1");
    }

    @Test
    @WithMockUser(username = "tenant@admin.com", roles = {"ADMIN"})
    @DisplayName("Denied switch creates ACCESS_DENIED audit log")
    void switchTenant_Denied_CreatesAuditLog() throws Exception {
        long auditCountBefore = auditLogRepo.count();

        mockMvc.perform(post("/api/tenant-switcher/switch")
                        .param("tenantId", "1")) // Unauthorized
                .andExpect(status().isForbidden());

        // Verify audit log created
        long auditCountAfter = auditLogRepo.count();
        assertEquals(auditCountBefore + 1, auditCountAfter,
                "Audit log should be created for denied switch");

        // Verify audit log details
        List<TenantAuditLog> logs = auditLogRepo.findAllByOrderByCreatedAtDesc(org.springframework.data.domain.Pageable.unpaged()).getContent();
        TenantAuditLog latestLog = logs.get(0);

        assertEquals("ACCESS_DENIED", latestLog.getAction(),
                "Action should be ACCESS_DENIED");

        assertNotNull(latestLog.getDetails(),
                "Details should contain reason");

        assertTrue(latestLog.getDetails().contains("UNAUTHORIZED_TENANT_SWITCH"),
                "Details should mention UNAUTHORIZED_TENANT_SWITCH");
    }

    // ============================================================================
    // Helper Methods
    // ============================================================================

    private Tenant createTenant(Long id, String name, String code, Tenant parent) {
        Tenant tenant = new Tenant();
        tenant.setId(id);
        tenant.setName(name);
        tenant.setCode(code);
        tenant.setParent(parent);
        tenant.setLevel(parent == null ? 0 : parent.getLevel() + 1);
        tenant.setActive(true);
        return tenantRepo.save(tenant);
    }
}
