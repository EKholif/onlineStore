package com.onlineStoreCom.entity.billing;

import com.onlineStoreCom.entity.setting.subsetting.IdBasedEntity;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "subscriptions")
public class Subscription extends IdBasedEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    @Column(name = "target_tenant_id", nullable = false)
    private Long targetTenantId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SubscriptionStatus status;

    @Column(name = "start_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Column(name = "next_billing_date")
    @Temporal(TemporalType.DATE)
    private Date nextBillingDate;

    public Subscription() {
    }

    public Plan getPlan() {
        return plan;
    }

    public void setPlan(Plan plan) {
        this.plan = plan;
    }

    public Long getTargetTenantId() {
        return targetTenantId;
    }

    public void setTargetTenantId(Long targetTenantId) {
        this.targetTenantId = targetTenantId;
    }

    public SubscriptionStatus getStatus() {
        return status;
    }

    public void setStatus(SubscriptionStatus status) {
        this.status = status;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getNextBillingDate() {
        return nextBillingDate;
    }

    public void setNextBillingDate(Date nextBillingDate) {
        this.nextBillingDate = nextBillingDate;
    }
}
