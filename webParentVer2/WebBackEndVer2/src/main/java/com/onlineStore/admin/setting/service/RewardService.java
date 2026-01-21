package com.onlineStore.admin.setting.service;

import com.onlineStore.admin.setting.RewardSettingRepository;
import com.onlineStore.admin.usersAndCustomers.customer.CustomerPointRepository;
import com.onlineStore.admin.usersAndCustomers.customer.repository.CustomersRepository;
import com.onlineStoreCom.entity.customer.Customer;
import com.onlineStoreCom.entity.customer.CustomerPoint;
import com.onlineStoreCom.entity.customer.CustomerPoint.PointType;
import com.onlineStoreCom.entity.setting.RewardSetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RewardService {

    @Autowired
    private RewardSettingRepository rewardSettingRepo;

    @Autowired
    private CustomerPointRepository customerPointRepo;

    @Autowired
    @Lazy // Avoid circular dep if CustomerService uses RewardService
    private CustomersRepository customerRepo;

    public void initDefaultSettings(Long tenantId) {
        if (rewardSettingRepo.findByTenant().isEmpty()) {
            RewardSetting setting = new RewardSetting();
            setting.setTenantId(tenantId);
            // Defaults are already set in entity
            rewardSettingRepo.save(setting);
        }
    }

    public RewardSetting getSettings() {
        return rewardSettingRepo.findByTenant().orElseGet(() -> {
            RewardSetting s = new RewardSetting();
            s.setEarningRate(0.0f);
            s.setRedeemRate(0.0f);
            s.setReviewPoints(0);
            s.setRegistrationPoints(0);
            s.setMinPointsToRedeem(0);
            s.setPointExpiryDays(0);
            return rewardSettingRepo.save(s);
        });
    }

    public void saveSettings(RewardSetting settings) {
        rewardSettingRepo.save(settings);
    }

    public void addPoints(Customer customer, int points, PointType type, String description) {
        if (points <= 0) return;

        CustomerPoint history = new CustomerPoint(customer, points, type, description);
        customerPointRepo.save(history);

        int currentBalance = customer.getPointsBalance();
        customer.setPointsBalance(currentBalance + points);
        customerRepo.save(customer);
    }

    public boolean redeemPoints(Customer customer, int pointsToRedeem) {
        RewardSetting settings = getSettings();
        if (pointsToRedeem < settings.getMinPointsToRedeem()) {
            return false; // Below threshold
        }

        int currentBalance = customer.getPointsBalance();
        if (currentBalance < pointsToRedeem) {
            return false; // Insufficient balance
        }

        CustomerPoint history = new CustomerPoint(customer, -pointsToRedeem, PointType.CHECKOUT_REDEEM, "Redeemed at checkout");
        customerPointRepo.save(history);

        customer.setPointsBalance(currentBalance - pointsToRedeem);
        customerRepo.save(customer);
        return true;
    }

    public int calculatePointsForAmount(float amount) {
        RewardSetting settings = getSettings();
        return (int) (amount * settings.getEarningRate());
    }

    public float calculateDiscountForPoints(int points) {
        RewardSetting settings = getSettings();
        return points * settings.getRedeemRate();
    }
}
