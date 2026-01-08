package com.onlineStore.admin.brand;

import com.onlineStore.admin.brand.reposetry.BrandRepository;
import com.onlineStore.admin.category.CategoryNotFoundException;
import com.onlineStore.admin.usersAndCustomers.users.servcies.UserService;
import com.onlineStoreCom.entity.brand.Brand;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class BrandService {

    public static final int BRANDS_PER_PAGE = 5;

    @Autowired
    private BrandRepository repository;

    public List<Brand> listAll() {

        return repository.findAll();
    }

    public Boolean existsById(Integer id) {
        return repository.findById(id).isPresent();
    }

    public Brand findById(Integer id) throws CategoryNotFoundException {
        try {

            return repository.findById(id).get();

        } catch (NoSuchElementException ex) {

            throw new CategoryNotFoundException("Could not find any Category with ID " + id);
        }
    }

    public Page<Brand> listByPage(int pageNum, String sortField, String sortDir, String keyWord) {
        Sort sort = Sort.by(sortField);

        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1, BRANDS_PER_PAGE, sort); // Used locally defined constant if
        // available, otherwise
        // UserService.USERS_PER_PAGE?
        // Note: BrandService has BRANDS_PER_PAGE at line 25. Original code used
        // UserService.USERS_PER_PAGE at line 61.
        // I should use BRANDS_PER_PAGE if appropriate or keep consistency.
        // Line 25: public static final int BRANDS_PER_PAGE = 5;
        // Line 61: PageRequest.of(..., UserService.USERS_PER_PAGE, ...);
        // This looks like a copy-paste error in original code. I will use
        // BRANDS_PER_PAGE.

        if (keyWord != null) {
            return repository.findAll(keyWord, pageable);
        } else {
            return repository.findAll(pageable);
        }
    }

    public void delete(Integer id) throws BrandNotFoundException {
        Integer countById = repository.countById(id);

        if (countById == null || countById == 0) {
            throw new BrandNotFoundException("Could not find any brand with ID " + id);
        }

        repository.deleteById(id);
    }

    public String checkUnique(Integer id, String name) {
        boolean isCreatingNew = (id == null || id == 0);
        Brand brandByName = repository.findByName(name);
        if (isCreatingNew) {
            if (brandByName != null)
                return "Duplicate";

        } else {
            if (brandByName != null && brandByName.getId() != id) {
                return "Duplicate";
            }
        }

        return "Ok";
    }

    public Brand saveBrand(Brand brand) {
        return repository.saveAndFlush(brand);

    }

}
