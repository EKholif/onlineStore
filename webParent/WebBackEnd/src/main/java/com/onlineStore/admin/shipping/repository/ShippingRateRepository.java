package com.onlineStore.admin.shipping.repository;

import com.onlineStoreCom.entity.setting.state.State;
import com.onlineStoreCom.entity.shipping.ShippingRate;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShippingRateRepository extends JpaRepository<ShippingRate, String> {

    @Query("SELECT sr FROM ShippingRate sr WHERE sr.country.id = ?1 AND sr.stateName = ?2")
    public ShippingRate findByCountryAndState(Integer countryId, String state);

    @Query("UPDATE ShippingRate sr SET sr.codSupported = ?2 WHERE sr.id = ?1")
    @Modifying
    public void updateCODSupport(Integer id, boolean enabled);

    @Query("SELECT sr FROM ShippingRate sr WHERE sr.country.name LIKE %?1% OR sr.stateName LIKE %?1%")
    public Page<ShippingRate> findAll(String keyword, Pageable pageable);
    @Query("SELECT sr FROM ShippingRate sr WHERE sr.stateName = ?1 ")
    public ShippingRate findByStateName(String stateName);
    public Integer countById(Integer id);
    @Query("SELECT sr.stateName FROM ShippingRate sr WHERE sr.id IS NOT NULL")
    List<String> findAllNonNullStateNames();


    @Modifying
    @Query("UPDATE ShippingRate sr SET sr.stateName = ?2 WHERE sr = ?1")
    public void updateStateName(ShippingRate shippingRate, State state);


    @Query("SELECT sr.stateName , sr.country FROM ShippingRate sr WHERE sr.stateName IS NULL")
    List<Object[]> findAllStateNameAndCountry();



}
