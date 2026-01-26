package com.onlineStore.admin.debug;

import com.onlineStoreCom.entity.setting.subsetting.IdBasedEntity;
import org.hibernate.annotations.Filter;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * AG-GOVERNANCE-001: Architecture Compliance Test.
 * Automatically enforces the "Tenant Aware Protocol" across the codebase.
 */
@SpringBootTest
public class ArchitectureComplianceTest {

    @Test
    public void verifyAllTenantEntitiesHaveFilter() {
        Reflections reflections = new Reflections("com.onlineStoreCom.entity");

        // Find all classes that look like they belong to a tenant (extend
        // IdBasedEntity)
        Set<Class<? extends IdBasedEntity>> tenantEntities = reflections.getSubTypesOf(IdBasedEntity.class);

        // Filter out abstract classes (MappedSuperclass)
        Set<Class<? extends IdBasedEntity>> concreteEntities = tenantEntities.stream()
                .filter(clazz -> !java.lang.reflect.Modifier.isAbstract(clazz.getModifiers()))
                .collect(Collectors.toSet());

        StringBuilder violationReport = new StringBuilder();
        boolean passed = true;

        System.out.println("AG-INFO: Scanning " + concreteEntities.size() + " entities for compliance...");

        for (Class<? extends IdBasedEntity> entity : concreteEntities) {
            if (!entity.isAnnotationPresent(Filter.class)) {
                violationReport.append("\n[VIOLATION] Entity '").append(entity.getSimpleName())
                        .append("' extends IdBasedEntity but is missing @Filter(name=\"tenantFilter\").");
                passed = false;
            } else {
                Filter filter = entity.getAnnotation(Filter.class);
                if (!"tenantFilter".equals(filter.name())) {
                    violationReport.append("\n[VIOLATION] Entity '").append(entity.getSimpleName())
                            .append("' has a Filter, but not the standard 'tenantFilter'. Found: '")
                            .append(filter.name()).append("'.");
                    passed = false;
                }
            }
        }

        if (!passed) {
            fail("Architecture Compliance Failed! " + violationReport.toString());
        } else {
            System.out.println("AG-INFO: All Checked Entities are Compliant.");
        }
    }
}
