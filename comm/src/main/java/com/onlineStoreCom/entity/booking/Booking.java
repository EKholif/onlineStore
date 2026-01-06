package com.onlineStoreCom.entity.booking;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.onlineStoreCom.entity.service.Service;
import com.onlineStoreCom.entity.setting.subsetting.IdBasedEntity;
import com.onlineStoreCom.entity.users.User;
import jakarta.persistence.*;
import org.hibernate.annotations.Filter;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
public class Booking extends IdBasedEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;

    // Customer who booked (assumed User entity represents customers too, or we
    // might need a distinct Customer entity if the system separates them)
    // Based on user_information, User seems to be the main actor.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @Column(name = "booking_time", nullable = false)
    private LocalDateTime bookingTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private BookingStatus status;

    @Column(length = 1024)
    private String notes;

    public Booking() {
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public User getCustomer() {
        return customer;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }

    public LocalDateTime getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(LocalDateTime bookingTime) {
        this.bookingTime = bookingTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
