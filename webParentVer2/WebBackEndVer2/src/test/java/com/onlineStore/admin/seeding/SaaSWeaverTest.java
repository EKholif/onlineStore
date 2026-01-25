package com.onlineStore.admin.seeding;

import com.onlineStore.admin.usersAndCustomers.users.UserRepository;
import com.onlineStoreCom.entity.tenant.Tenant;
import com.onlineStoreCom.entity.users.User;
import com.onlineStoreCom.repo.TenantRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class SaaSWeaverTest {

    @Autowired
    private TenantRepository tenantRepo;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private SaaSWeaverSeeder seeder;

    @Test
    public void testHierarchySeeding() throws Exception {
        // Run Seeder
        seeder.run();

        // 1. Verify Tenants
        Optional<Tenant> master = tenantRepo.findByCode("saas_master");
        Optional<Tenant> agency = tenantRepo.findByCode("web_dev_pro");
        Optional<Tenant> child = tenantRepo.findByCode("joes_pizza");

        assertThat(master).isPresent();
        assertThat(agency).isPresent();
        assertThat(child).isPresent();

        // Verify Parent Link
        assertThat(child.get().getParent().getId()).isEqualTo(agency.get().getId());

        // 2. Verify Agency Admin Access (N:N Mapping)
        User agencyUser = userRepo.findByEmail("admin@webdevpro.com");
        assertThat(agencyUser).isNotNull();

        // Must have access to at least 2 tenants (Agency itself + Child)
        assertThat(agencyUser.getTenants().size()).isGreaterThanOrEqualTo(2);

        boolean hasAccessToChild = agencyUser.getTenants().stream()
                .anyMatch(ut -> ut.getTenant().getCode().equals("joes_pizza"));

        assertThat(hasAccessToChild).isTrue().as("Agency Admin must have access to Child Tenant");
    }
}
