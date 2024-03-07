package com.onlineStore.admin.brand;

import com.onlineStoreCom.entity.brand.Brand;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Transactional
public class BrandService {


    public static final int BRANDS_PER_PAGE = 10;

    @Autowired
    private BrandRepository repo;

    public List<Brand> listAll() {
        repo.findAll().forEach(brand -> System.out.println(brand.getName()));
        return  repo.findAll();
    }

    public void delete(Long id) throws BrandNotFoundException {
        Long countById = repo.countById(id);

        if (countById == null || countById == 0) {
            throw new BrandNotFoundException("Could not find any brand with ID " + id);
        }

        repo.deleteById(id);
    }
    public String checkUnique(Integer id, String name) {
        boolean isCreatingNew = (id == null || id == 0);
        Brand brandByName = repo.findByName(name);

        if (isCreatingNew) {
            if (brandByName != null) return "Duplicate";
        } else {
            if (brandByName != null && brandByName.getId() != id) {
                return "Duplicate";
            }
        }

        return "OK";
    }



}
