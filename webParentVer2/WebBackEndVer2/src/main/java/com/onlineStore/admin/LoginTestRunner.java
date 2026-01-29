package com.onlineStore.admin;

import com.onlineStore.admin.usersAndCustomers.users.UserRepository;
import com.onlineStoreCom.entity.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@Order(200)
public class LoginTestRunner implements CommandLineRunner {

    @Autowired
    private UserRepository repo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("🔐 [LoginTestRunner] STARTING CREDENTIAL CHECK...");

        List<String> targets = Arrays.asList(
                "root@saasmaster.com",
                "admin@store.com",
                "admin@car.com",
                "admin@food.com"
        );

        for (String email : targets) {
            System.out.println("   Checking User: " + email);

            // Use Generic Search method we fixed earlier
            User user = repo.findByEmailGeneric(email);

            if (user == null) {
                System.out.println("      ❌ NOT FOUND in Database.");
                continue;
            }

            System.out.println("      ✅ FOUND (ID: " + user.getId() + ", Tenant: " + user.getTenantId() + ")");

            // Test Empty Password
            boolean matchEmpty = passwordEncoder.matches("", user.getPassword());
            System.out.println("      🔑 Password matches EMPTY string? " + (matchEmpty ? "YES ✅" : "NO ❌"));

            // Test Default Password hints (if check failed)
            if (!matchEmpty) {
                if (passwordEncoder.matches("Root1234!", user.getPassword()))
                    System.out.println("      💡 Password matches 'Root1234!'");
                else if (passwordEncoder.matches("password", user.getPassword()))
                    System.out.println("      💡 Password matches 'password'");
                else if (passwordEncoder.matches("admin", user.getPassword()))
                    System.out.println("      💡 Password matches 'admin'");
                else if (passwordEncoder.matches("123456", user.getPassword()))
                    System.out.println("      💡 Password matches '123456'");
                else
                    System.out.println("      ❓ Password is set to unknown value.");
            }
        }
        System.out.println("🔐 [LoginTestRunner] CHECK COMPLETE.");
    }
}
