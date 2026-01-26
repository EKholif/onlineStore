package com.onlineStore.admin.setting;

import com.onlineStoreCom.entity.setting.RewardSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RewardSettingRepository extends JpaRepository<RewardSetting, Integer> {

    // As per tenant filter, this returns the single setting row for the current
    // tenant
    @Query("SELECT r FROM RewardSetting r")
    Optional<RewardSetting> findByTenant();
}
