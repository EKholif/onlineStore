package com.onlineStore.admin.usersAndCustomers.customer.service;

import com.onlineStore.admin.usersAndCustomers.customer.repository.CustomersRepository;
import com.onlineStoreCom.entity.customer.Customer;
import com.onlineStoreCom.tenant.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;

public class CustomerUniqueEmailTest {

    @Mock
    private CustomersRepository repo;

    @InjectMocks
    private CustomerService service;

    private MockedStatic<TenantContext> tenantContextMock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        tenantContextMock = Mockito.mockStatic(TenantContext.class);
    }

    @AfterEach
    void tearDown() {
        tenantContextMock.close();
    }

    @Test
    void testIsEmailUnique_SameTenant_Duplicate() {
        // Arrange
        String email = "duplicate@example.com";
        Long tenantId = 1L;

        tenantContextMock.when(TenantContext::getTenantId).thenReturn(tenantId);

        Customer existing = new Customer();
        existing.setId(100);
        existing.setEmail(email);
        existing.setTenantId(tenantId);

        Mockito.when(repo.findByEmailAndTenantId(eq(email), eq(tenantId))).thenReturn(existing);

        // Act: Try to create NEW customer (id=null) with same email in SAME tenant
        boolean unique = service.isEmailUnique(null, email);

        // Assert
        assertThat(unique).isFalse();
    }

    @Test
    void testIsEmailUnique_DifferentTenant_Allowed() {
        // Arrange
        String email = "duplicate@example.com";
        Long myTenantId = 2L;

        tenantContextMock.when(TenantContext::getTenantId).thenReturn(myTenantId);

        // Repo returns null because for Tenant 2, this email doesn't exist
        // (even if it exists for Tenant 1, the query filters by TenantID)
        Mockito.when(repo.findByEmailAndTenantId(eq(email), eq(myTenantId))).thenReturn(null);

        // Act
        boolean unique = service.isEmailUnique(null, email);

        // Assert
        assertThat(unique).isTrue();
    }

    @Test
    void testIsEmailUnique_EditingSelf_Allowed() {
        // Arrange
        String email = "me@example.com";
        Long tenantId = 1L;
        int myId = 55;

        tenantContextMock.when(TenantContext::getTenantId).thenReturn(tenantId);

        Customer me = new Customer();
        me.setId(myId);
        me.setEmail(email);

        Mockito.when(repo.findByEmailAndTenantId(email, tenantId)).thenReturn(me);

        // Act: Edit myself
        boolean unique = service.isEmailUnique(myId, email);

        // Assert
        assertThat(unique).isTrue();
    }
}
