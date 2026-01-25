package com.onlineStoreCom.entity.billing;

import com.onlineStoreCom.entity.setting.subsetting.IdBasedEntity;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "plans")
public class Plan extends IdBasedEntity {

    @Column(nullable = false, unique = true, length = 128)
    private String name;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false, length = 20)
    private String billingCycle; // MONTHLY, YEARLY

    @Column(length = 2048)
    private String description;

    @Column(name = "commission_rate", nullable = false)
    private double commissionRate = 0.0; // Percentage of sales (0.0 to 100.0)

    @ElementCollection
    @CollectionTable(name = "plan_features", joinColumns = @JoinColumn(name = "plan_id"))
    @Column(name = "feature")
    private Set<String> features = new HashSet<>();

    public Plan() {
    }

    public Plan(String name, double price, String billingCycle) {
        this.name = name;
        this.price = price;
        this.billingCycle = billingCycle;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getBillingCycle() {
        return billingCycle;
    }

    public void setBillingCycle(String billingCycle) {
        this.billingCycle = billingCycle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getCommissionRate() {
        return commissionRate;
    }

    public void setCommissionRate(double commissionRate) {
        this.commissionRate = commissionRate;
    }

    public Set<String> getFeatures() {
        return features;
    }

    public void setFeatures(Set<String> features) {
        this.features = features;
    }
}
