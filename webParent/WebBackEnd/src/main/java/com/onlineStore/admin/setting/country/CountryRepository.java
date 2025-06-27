package com.onlineStore.admin.setting.country;

import com.onlineStoreCom.entity.setting.Country.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CountryRepository extends JpaRepository<Country, String> {


    List<Country> findAllByOrderByNameAsc();
}
