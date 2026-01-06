package com.onlineStore.admin.setting.country;

import com.onlineStoreCom.entity.setting.state.Country.Country;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CounteryRestController {
    @Autowired
    private CountryRepository repo;

    @GetMapping("/setting/list")
    public List<Country> countryList() {
        List<Country> countryList = repo.findAllByOrderByNameAsc();
        return countryList;

    }

    @PostMapping("/setting/save")
    public String saveCountry(@RequestBody Country country) {

        Country savedCountry = repo.saveAndFlush(country);
        return String.valueOf(savedCountry.getId());

    }

    @PostMapping("/countries/save")
    public String save(@RequestBody Country country) {
        Country savedCountry = repo.save(country);
        return String.valueOf(savedCountry.getId());
    }

    @DeleteMapping("/countries/delete/{id}")
    public void delete(@PathVariable("id") Integer id) {
        repo.deleteById(id);
    }
}
