package com.onlineStoreCom.entity.customer;

import com.onlineStoreCom.entity.setting.subsetting.IdBasedEntity;
import com.onlineStoreCom.tenant.TenantAware;
import com.onlineStoreCom.tenant.TenantListener;
import jakarta.persistence.*;
import org.hibernate.annotations.Filter;

import java.util.Date;

@Entity
@Table(name = "customer_points_history")
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
@EntityListeners(TenantListener.class)
public class CustomerPoint extends IdBasedEntity implements TenantAware {

    @Column(name = "tenant_id", updatable = false)
    private Long tenantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(nullable = false)
    private int points; // Positive for earning, Negative for spending

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private PointType type;

    @Column(length = 255)
    private String description; // description of the event

    @Column(name = "transaction_date")
    private Date transactionDate;

    public enum PointType {
        ORDER_EARN,
        REVIEW_EARN,
        SIGNUP_BONUS,
        CHECKOUT_REDEEM,
        ADMIN_ADJUSTMENT
    }

    public CustomerPoint() {
        this.transactionDate = new Date();
    }

    public CustomerPoint(Customer customer, int points, PointType type, String description) {
        this.customer = customer;
        this.points = points;
        this.type = type;
        this.description = description;
        this.transactionDate = new Date();
    }

    @Override
    public Long getTenantId() {
        return tenantId;
    }

    @Override
    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public PointType getType() {
        return type;
    }

    public void setType(PointType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }
}
