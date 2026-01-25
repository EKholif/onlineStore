package com.onlineStore.services.service.repository;

import com.onlineStore.services.common.repository.SearchRepository;
import com.onlineStoreCom.entity.booking.Booking;
import com.onlineStoreCom.entity.booking.BookingType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends SearchRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b WHERE b.name LIKE %?1% OR b.customerName LIKE %?1%")
    Page<Booking> findAll(String keyword, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.bookingType = ?1")
    Page<Booking> findByBookingType(BookingType type, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.bookingType = ?1 AND (b.name LIKE %?2% OR b.customerName LIKE %?2%)")
    Page<Booking> findByBookingTypeAndKeyword(BookingType type, String keyword, Pageable pageable);

    Long countById(Long id);
}
