package com.onlineStore.admin.saas;

import com.onlineStore.admin.saas.repository.BusinessDomainRepository;
import com.onlineStore.admin.saas.repository.BusinessTemplateRepository;
import com.onlineStoreCom.entity.saas.BusinessDomain;
import com.onlineStoreCom.entity.saas.BusinessTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class SaaSStructureTest {

    @Autowired
    private BusinessDomainRepository domainRepository;

    @Autowired
    private BusinessTemplateRepository templateRepository;

    @Test
    public void testCreateDomainAndTemplates() {
        // 1. Create Domain
        BusinessDomain domain = new BusinessDomain("TEST_DOMAIN");
        domain.setDescription("Test Description");
        BusinessDomain savedDomain = domainRepository.save(domain);

        assertThat(savedDomain.getId()).isGreaterThan(0);

        // 2. Create Template attached to Domain
        BusinessTemplate template = new BusinessTemplate("TEST_TEMPLATE", savedDomain);
        template.setDescription("Template Desc");
        template.setDefaultFeatures("{\"feature\": true}");
        
        BusinessTemplate savedTemplate = templateRepository.save(template);

        assertThat(savedTemplate.getId()).isGreaterThan(0);
        assertThat(savedTemplate.getBusinessDomain().getId()).isEqualTo(savedDomain.getId());

        // 3. Verify Reverse Relation
        // Clear persistence context to force fetch from DB
        // (In a real integration test, we might need EntityManager, 
        // but finding by domain is enough to prove relation)
        Iterable<BusinessTemplate> templates = templateRepository.findByBusinessDomain(savedDomain);
        assertThat(templates).hasSizeGreaterThanOrEqualTo(1);
        
        // Clean up
        templateRepository.delete(savedTemplate);
        domainRepository.delete(savedDomain);
    }
}
