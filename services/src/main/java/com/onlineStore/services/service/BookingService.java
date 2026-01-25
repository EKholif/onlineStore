package com.onlineStore.services.service;

import com.onlineStore.services.service.repository.BookingRepository;
import com.onlineStoreCom.entity.booking.Booking;
import com.onlineStoreCom.entity.booking.BookingType;
import com.onlineStoreCom.entity.exception.BookingNotFoundException;
import com.onlineStoreCom.tenant.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@Transactional
public class BookingService {

    public static final int BOOKINGS_PER_PAGE = 10;

    @Autowired
    private BookingRepository repo;

    public Page<Booking> listByPage(int pageNum, String sortField, String sortDir, String keyword, BookingType type) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1, BOOKINGS_PER_PAGE, sort);

        if (keyword != null && !keyword.isEmpty()) {
            if (type != null) {
                return repo.findByBookingTypeAndKeyword(type, keyword, pageable);
            }
            return repo.findAll(keyword, pageable);
        }

        if (type != null) {
            return repo.findByBookingType(type, pageable);
        }

        return repo.findAll(pageable);
    }

    public Page<Booking> listByPage(int pageNum, String sortField, String sortDir, String keyword) {
        return listByPage(pageNum, sortField, sortDir, keyword, null);
    }

    public Booking save(Booking booking) {
        if (booking.getId() == null) {
            booking.setTenantId(TenantContext.getTenantId());
        }
        return repo.save(booking);
    }

    public Booking get(Long id) throws BookingNotFoundException {
        try {
            return repo.findById(id).get();
        } catch (NoSuchElementException ex) {
            throw new BookingNotFoundException("Could not find any booking with ID " + id);
        }
    }

    public void delete(Long id) throws BookingNotFoundException {
        Long count = repo.countById(id);
        if (count == null || count == 0) {
            throw new BookingNotFoundException("Could not find any booking with ID " + id);
        }
        repo.deleteById(id);
    }
}
