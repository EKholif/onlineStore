package com.onlineStore.admin.seeding;

import com.onlineStore.admin.usersAndCustomers.users.UserRepository;
import com.onlineStore.admin.usersAndCustomers.users.role.RoleRepository;
import com.onlineStoreCom.entity.tenant.Tenant;
import com.onlineStoreCom.entity.tenant.TenantStatus;
import com.onlineStoreCom.entity.users.Role;
import com.onlineStoreCom.entity.users.User;
import com.onlineStoreCom.repo.TenantRepository;
import com.onlineStoreCom.tenant.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;

@Component
public class SaaSWeaverSeeder implements CommandLineRunner {

    @Autowired
    private TenantRepository tenantRepo;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private RoleRepository roleRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("[SaaSWeaver] Starting Hierarchy Seeding...");

        // 1. Seed Tenants
        Tenant saasMaster = seedTenant("SaaS Master", "saas_master", null);
        Tenant webDevPro = seedTenant("WebDev Pro", "web_dev_pro", null); // Independent Agency
        Tenant joesPizza = seedTenant("Joe's Pizza", "joes_pizza", webDevPro); // Child of Agency

        // 2. Seed Roles (Global)
        Role adminRole = seedRole("Admin", "Full Administrator");
        Role editorRole = seedRole("Editor", "Content Editor");

        // 3. Seed Users & Links
        // Root Admin -> Access to SaaS Master (0)
        seedUser("root@saasmaster.com", "Root1234!", "Super", "Admin", adminRole, saasMaster);

        // Agency Admin -> Access to WebDev Pro
        User agencyUser = seedUser("admin@webdevpro.com", "Agency1234!", "Agency", "Admin", adminRole, webDevPro);

        // Child Admin -> Access to Joe's Pizza
        seedUser("manager@joespizza.com", "Pizza1234!", "Joe", "Manager", adminRole, joesPizza);

        // [AGENCY MAGIC] Link Agency Admin to Child Tenant as well (so they can switch
        // view)
        linkUserToTenant(agencyUser, joesPizza);

        System.out.println("[SaaSWeaver] Seeding Complete.");
    }

    private Tenant seedTenant(String name, String code, Tenant parent) {
        Optional<Tenant> existing = tenantRepo.findByCode(code);
        if (existing.isPresent()) {
            return existing.get();
        }
        Tenant t = new Tenant(name, code, TenantStatus.ACTIVE, new Date());
        t.setParent(parent);
        return tenantRepo.save(t);
    }

    private Role seedRole(String name, String desc) {
        // [AG-FIX] Handle duplicate roles in DB by fetching all and filtering
        return roleRepo.findAll().stream()
                .filter(r -> r.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    private User seedUser(String email, String rawPwd, String first, String last, Role role, Tenant homeTenant) {
        // Note: We temporary set Context to homeTenant to check existence safely if
        // needed,
        // but User is global-ish in DB but filtered by tenant_id.
        // With N:N, we should check by email globally.

        // FAIL SAFE: Switch to System Context (Null) to search globally
        TenantContext.clear();
        User user = userRepo.findByEmail(email);

        if (user == null) {
            user = new User(email, passwordEncoder.encode(rawPwd), first, last);
            user.setEnabled(true);
            user.addRole(role);
            // Legacy field - keep for compatibility
            user.setTenantId(homeTenant.getId());

            // N:N Link
            user.addTenant(homeTenant);

            user = userRepo.save(user);
            System.out.println("Seeded User: " + email);
        }
        TenantContext.clear();
        return user;
    }

    private void linkUserToTenant(User user, Tenant tenant) {
        TenantContext.clear(); // System Context
        // Reload user to ensure attached state if needed, or just check relationships
        boolean alreadyLinked = user.getTenants().stream()
                .anyMatch(ut -> ut.getTenant().getId().equals(tenant.getId()));

        if (!alreadyLinked) {
            user.addTenant(tenant);
            userRepo.save(user); // Cascade updates UserTenant table
            System.out.println("Linked User " + user.getEmail() + " to Tenant " + tenant.getName());
        }
        TenantContext.clear();
    }
}
