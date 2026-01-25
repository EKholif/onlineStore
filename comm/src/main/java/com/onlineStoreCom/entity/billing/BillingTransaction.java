package com.onlineStoreCom.entity.billing;

import com.onlineStoreCom.entity.setting.subsetting.IdBasedEntity;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "billing_transactions")
public class BillingTransaction extends IdBasedEntity {

    @Column(nullable = false)
    private double amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private BillingType type;

    @Column(name = "source_tenant_id", nullable = false)
    private Long sourceTenantId;

    @Column(name = "reference_id", nullable = true)
    private String referenceId; // e.g., "ORDER-12345" or "SUB-99"

    @Column(name = "transaction_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date transactionDate;

    public BillingTransaction() {
    }

    public BillingTransaction(double amount, BillingType type, Long sourceTenantId, String referenceId) {
        this.amount = amount;
        this.type = type;
        this.sourceTenantId = sourceTenantId;
        this.referenceId = referenceId;
        this.transactionDate = new Date();
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public BillingType getType() {
        return type;
    }

    public void setType(BillingType type) {
        this.type = type;
    }

    public Long getSourceTenantId() {
        return sourceTenantId;
    }

    public void setSourceTenantId(Long sourceTenantId) {
        this.sourceTenantId = sourceTenantId;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }
}
