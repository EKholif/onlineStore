package com.onlineStore.admin;

import com.onlineStore.admin.category.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import jakarta.transaction.Transactional;

@Component
public class DataRepairRunner implements CommandLineRunner {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("🔧 RUNNING DATA REPAIR: Fixing Broken Category Hierarchies...");
        try {
            categoryRepository.fixBrokenHierarchies();
            System.out.println("✅" +
                    " DATA REPAIR COMPLETE: Cross-tenant parents removed.");
        } catch (Exception e) {
            System.err.println("❌ DATA REPAIR FAILED: " + e.getMessage());
        }
    }
}


