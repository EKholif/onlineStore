package com.onlineStoreCom.entity.setting;

import com.onlineStoreCom.entity.setting.subsetting.IdBasedEntity;
import com.onlineStoreCom.tenant.TenantAware;
import com.onlineStoreCom.tenant.TenantListener;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import org.hibernate.annotations.Filter;

@Entity
@Table(name = "reward_settings")
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
@EntityListeners(TenantListener.class)
public class RewardSetting extends IdBasedEntity implements TenantAware {

    @Column(name = "tenant_id", updatable = false)
    private Long tenantId;

    @Column(name = "earning_rate", nullable = false)
    private float earningRate = 1.0f; // Points per currency unit (e.g., 1 $ = 1 Point)

    @Column(name = "redeem_rate", nullable = false)
    private float redeemRate = 0.01f; // Currency value per point (e.g., 100 Points = 1 $)

    @Column(name = "sign_up_points")
    private int signUpPoints = 0;

    @Column(name = "point_expiry_days")
    private int pointExpiryDays = 0;

    // Alias for signUpPoints to match Ver2 Service usage
    public void setRegistrationPoints(int points) {
        this.signUpPoints = points;
    }

    public int getRegistrationPoints() {
        return this.signUpPoints;
    }
    
    public int getPointExpiryDays() {
        return pointExpiryDays;
    }

    public void setPointExpiryDays(int pointExpiryDays) {
        this.pointExpiryDays = pointExpiryDays;
    }

    @Column(name = "review_points")
    private int reviewPoints = 10;

    @Column(name = "min_points_to_redeem")
    private int minPointsToRedeem = 100;

    public RewardSetting() {
    }

    @Override
    public Long getTenantId() {
        return tenantId;
    }

    @Override
    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public float getEarningRate() {
        return earningRate;
    }

    public void setEarningRate(float earningRate) {
        this.earningRate = earningRate;
    }

    public float getRedeemRate() {
        return redeemRate;
    }

    public void setRedeemRate(float redeemRate) {
        this.redeemRate = redeemRate;
    }

    public int getSignUpPoints() {
        return signUpPoints;
    }

    public void setSignUpPoints(int signUpPoints) {
        this.signUpPoints = signUpPoints;
    }

    public int getReviewPoints() {
        return reviewPoints;
    }

    public void setReviewPoints(int reviewPoints) {
        this.reviewPoints = reviewPoints;
    }

    public int getMinPointsToRedeem() {
        return minPointsToRedeem;
    }

    public void setMinPointsToRedeem(int minPointsToRedeem) {
        this.minPointsToRedeem = minPointsToRedeem;
    }
}
