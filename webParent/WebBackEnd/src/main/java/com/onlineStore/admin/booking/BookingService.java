package com.onlineStore.admin.booking;

import com.onlineStore.admin.utility.paging.PagingAndSortingHelper;
import com.onlineStoreCom.entity.booking.Booking;
import com.onlineStoreCom.entity.booking.BookingType;
import com.onlineStoreCom.tenant.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.NoSuchElementException;

@Service
@Transactional
public class BookingService {

    @Autowired
    private BookingRepository repo;

    public void listByPage(int pageNum, PagingAndSortingHelper helper, BookingType type) {
        helper.listEntities(pageNum, 10, repo);
    }

    public Page<Booking> listByPage(int pageNum, String sortField, String sortDir, String keyword, BookingType type) {
        // Custom logic if helper isn't enough or need filtering by type
        return null; // Simplified for now, relying on Controller to use Repository or Helper
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
