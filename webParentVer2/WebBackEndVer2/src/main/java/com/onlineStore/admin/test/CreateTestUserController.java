package com.onlineStore.admin.test;

import com.onlineStore.admin.usersAndCustomers.users.UserRepository;
import com.onlineStore.admin.usersAndCustomers.users.role.RoleRepository;
import com.onlineStoreCom.entity.tenant.Tenant;
import com.onlineStoreCom.entity.users.Role;
import com.onlineStoreCom.entity.users.User;
import com.onlineStoreCom.entity.users.UserTenant;
import com.onlineStoreCom.repo.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;

/**
 * TEMPORARY TEST CONTROLLER - DELETE AFTER USE
 * Creates test Platform Admin user: empt@admin.com / 11
 */
@RestController
@RequestMapping("/test")
public class CreateTestUserController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private TenantRepository tenantRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/create-admin")
    public String createTestAdmin() {
        try {
            // Check if user already exists
            if (userRepo.findByEmail("empt@admin.com") != null) {
                return "User already exists!";
            }

            // Get Platform Admin role (ID = 1)
            Role platformAdminRole = roleRepo.findById(1).orElseThrow();

            // Get SaaS Master tenant (ID = 0)
            Tenant masterTenant = tenantRepo.findById(0L).orElseThrow();

            // Create user
            User user = new User();
            user.setEmail("empt@admin.com");
            user.setPassword(passwordEncoder.encode("11"));
            user.setFirstName("Test");
            user.setLastName("Admin");
            user.setEnabled(true);
            user.setTenantId(0L);

            // Add role
            Set<Role> roles = new HashSet<>();
            roles.add(platformAdminRole);
            user.setRoles(roles);

            // Add tenant
            Set<UserTenant> userTenants = new HashSet<>();
            UserTenant ut = new UserTenant();
            ut.setUser(user);
            ut.setTenant(masterTenant);
            userTenants.add(ut);
            user.setTenants(userTenants);

            // Save
            userRepo.save(user);

            return "SUCCESS! User created: empt@admin.com / 11 (Platform Admin)";

        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }
}
