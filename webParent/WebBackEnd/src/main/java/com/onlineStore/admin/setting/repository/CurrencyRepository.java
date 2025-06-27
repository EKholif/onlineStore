package com.onlineStore.admin.setting.repository;

import com.onlineStoreCom.entity.setting.subsetting.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, String> {


    List<Currency> findAllByOrderByNameAsc();
}
