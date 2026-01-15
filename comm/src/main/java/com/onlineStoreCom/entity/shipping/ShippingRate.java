package com.onlineStoreCom.entity.shipping;

import com.onlineStoreCom.entity.setting.state.Country.Country;
import com.onlineStoreCom.entity.setting.subsetting.IdBasedEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "shipping_rates")
@org.hibernate.annotations.Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
public class ShippingRate extends IdBasedEntity {

    private float rate;
    private int days;
    @Column(name = "state")
    private String stateName;
    @Column(name = "cod_supported")
    private boolean codSupported;
    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;

    public ShippingRate(float rate) {
        this.rate = rate;

    }

    public ShippingRate(String stateName) {
        this.stateName = stateName;
    }

    public ShippingRate() {

    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public boolean isCodSupported() {
        return codSupported;
    }

    public void setCodSupported(boolean codSupported) {
        this.codSupported = codSupported;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return "ShippingRate [id=" + id + ", rate=" + rate + ", days=" + days + ", codSupported=" + codSupported
                + ", country=" + country.getName() + ", state=" + stateName + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ShippingRate other = (ShippingRate) obj;
        if (id == null) {
            return other.id == null;
        } else
            return id.equals(other.id);
    }

}
