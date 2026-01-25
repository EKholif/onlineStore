package com.onlineStoreCom.entity.booking;

import com.onlineStoreCom.entity.setting.subsetting.IdBasedEntity;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "bookings")
public class Booking extends IdBasedEntity {

    @Column(nullable = false, length = 150)
    private String name; // Doctor Name, Room Name, or Class Name

    @Enumerated(EnumType.STRING)
    @Column(name = "booking_type", nullable = false, length = 20)
    private BookingType bookingType;

    @Column(name = "customer_name", nullable = false, length = 150)
    private String customerName;

    @Column(name = "start_time")
    @Temporal(TemporalType.TIMESTAMP)
    @org.springframework.format.annotation.DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private Date startTime;

    @Column(name = "end_time")
    @Temporal(TemporalType.TIMESTAMP)
    @org.springframework.format.annotation.DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private Date endTime;

    @Column(length = 20)
    private String status; // PENDING, CONFIRMED, CANCELLED

    @Column(length = 500)
    private String details; // Extra details like "Room 101" or "Chapter 4"

    @Column(name = "tenant_id")
    private Long tenantId;

    public Booking() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BookingType getBookingType() {
        return bookingType;
    }

    public void setBookingType(BookingType bookingType) {
        this.bookingType = bookingType;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }
}
