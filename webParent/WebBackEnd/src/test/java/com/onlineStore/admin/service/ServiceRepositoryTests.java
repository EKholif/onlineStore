package com.onlineStore.admin.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.onlineStore.services.service.repository.ServiceRepository;
import com.onlineStoreCom.entity.service.Service;
import com.onlineStoreCom.entity.service.ServiceLocationType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(true)
@org.springframework.boot.autoconfigure.domain.EntityScan({"com.onlineStoreCom.entity"})
@org.springframework.data.jpa.repository.config.EnableJpaRepositories({"com.onlineStore.services.service.repository"})
public class ServiceRepositoryTests {

    @Autowired
    private ServiceRepository repo;

    @Test
    public void testCreateService() {
        Service service = new Service();
        long timestamp = System.currentTimeMillis();
        service.setName("Test Service " + timestamp);
        service.setAlias("test-service-" + timestamp);
        service.setShortDescription("Short description");
        service.setFullDescription("Full description");
        service.setPrice(100.0f);
        service.setCost(50.0f);
        service.setEnabled(true);
        service.setDuration(60); // In minutes
        service.setBufferTime(15); // In minutes
        service.setLocationType(ServiceLocationType.ONSITE_BUSINESS);
        service.setTenantId(1L);
        service.setCreatedTime(new java.util.Date());
        service.setUpdatedTime(new java.util.Date());
        service.setMainImage("default.png");

        Service savedService = repo.save(service);

        assertThat(savedService).isNotNull();
        assertThat(savedService.getId()).isGreaterThan(0);
    }

    @Test
    public void testListAllService() {
        Service service = new Service();
        long timestamp = System.currentTimeMillis();
        service.setName("Test Service List " + timestamp);
        service.setAlias("test-service-list-" + timestamp);
        service.setShortDescription("Short description");
        service.setFullDescription("Full description");
        service.setPrice(100.0f);
        service.setCost(50.0f);
        service.setEnabled(true);
        service.setDuration(60);
        service.setBufferTime(15);
        service.setLocationType(ServiceLocationType.ONSITE_BUSINESS);
        service.setTenantId(1L);
        service.setCreatedTime(new java.util.Date());
        service.setUpdatedTime(new java.util.Date());
        service.setMainImage("default.png");

        repo.save(service);

        Iterable<Service> services = repo.findAll();
        assertThat(services).isNotEmpty();
        services.forEach(System.out::println);
    }
}
