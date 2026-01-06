package com.onlineStoreCom.entity.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.onlineStoreCom.entity.base.CatalogItem;
import jakarta.persistence.*;
import org.hibernate.annotations.Filter;

@Entity
@Table(name = "services")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
public class Service extends CatalogItem {

    private int duration; // In minutes

    private int bufferTime; // In minutes, setup/cleanup time methods

    @Enumerated(EnumType.STRING)
    @Column(name = "location_type")
    private ServiceLocationType locationType; // ONLINE, ONSITE

    public Service() {
    }

    public Service(String name) {
        this.name = name;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getBufferTime() {
        return bufferTime;
    }

    public void setBufferTime(int bufferTime) {
        this.bufferTime = bufferTime;
    }

    public ServiceLocationType getLocationType() {
        return locationType;
    }

    public void setLocationType(ServiceLocationType locationType) {
        this.locationType = locationType;
    }

    @Transient
    public String getImagePath() {
        String dirName = "/services-photos/";
        if (id == null || mainImage == null)
            return "/images/default-service.png";
        return dirName + this.getTenantId() + "/" + this.id + "/" + this.mainImage;
    }
}
