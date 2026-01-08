package com.onlineStore.services.service;

import com.onlineStore.services.service.repository.ServiceRepository;
import com.onlineStoreCom.entity.service.Service;
import com.onlineStoreCom.exception.ServiceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@org.springframework.stereotype.Service
@Transactional
public class ServiceService {
    public static final int SERVICES_PER_PAGE = 4;

    @Autowired
    private ServiceRepository repo;

    public List<Service> listAll() {
        return (List<Service>) repo.findAll();
    }

    public Page<Service> listByPage(int pageNum, String sortField, String sortDir, String keyword) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNum - 1, SERVICES_PER_PAGE, sort);

        if (keyword != null) {
            return repo.findAll(keyword, pageable);
        }
        return repo.findAll(pageable);
    }

    public Service save(Service service) {
        if (service.getId() == null) {
            service.setCreatedTime(new Date());
        }
        service.setUpdatedTime(new Date());

        if (service.getAlias() == null || service.getAlias().isEmpty()) {
            service.setAlias(service.getName().replaceAll(" ", "-"));
        } else {
            service.setAlias(service.getAlias().replaceAll(" ", "-"));
        }

        return repo.save(service);
    }

    public Service get(Integer id) throws ServiceNotFoundException {
        try {
            return repo.findById(id).get();
        } catch (NoSuchElementException ex) {
            throw new ServiceNotFoundException("Could not find any service with ID " + id);
        }
    }

    public void delete(Integer id) throws ServiceNotFoundException {
        Long count = repo.countById(id);
        if (count == null || count == 0) {
            throw new ServiceNotFoundException("Could not find any service with ID " + id);
        }
        repo.deleteById(id);
    }

    public String checkUnique(Integer id, String name) {
        boolean isCreatingNew = (id == null || id == 0);
        Service serviceByName = repo.findByName(name);

        if (isCreatingNew) {
            if (serviceByName != null)
                return "Duplicate";
        } else {
            if (serviceByName != null && serviceByName.getId() != id) {
                return "Duplicate";
            }
        }
        return "OK";
    }

    public void updateServiceEnabledStatus(Integer id, boolean enabled) {
        repo.updateEnabledStatus(id, enabled);
    }
}
