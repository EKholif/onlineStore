package com.onlineStore.admin.booking;

import com.onlineStore.admin.utility.paging.PagingAndSortingHelper;
import com.onlineStore.admin.utility.paging.PagingAndSortingParam;
import com.onlineStore.services.service.BookingService;
import com.onlineStoreCom.entity.booking.Booking;
import com.onlineStoreCom.entity.booking.BookingType;
import com.onlineStoreCom.entity.exception.BookingNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class BookingController {

    @Autowired
    private BookingService service;

    @GetMapping("/bookings")
    public String listFirstPage(Model model) {
        return "redirect:/bookings/page/1?sortField=name&sortDir=asc";
    }

    @GetMapping("/bookings/page/{pageNum}")
    public String listByPage(
            @PagingAndSortingParam(listName = "listBookings", moduleURL = "/bookings") PagingAndSortingHelper helper,
            @PathVariable(name = "pageNum") int pageNum,
            @RequestParam(name = "type", required = false) BookingType type,
            Model model) {

        Page<Booking> page = service.listByPage(pageNum, helper.getSortField(), helper.getSortDir(),
                helper.getKeyword(), type);
        helper.updateModelAttributes(pageNum, page);

        model.addAttribute("selectedType", type);
        return "booking/bookings";
    }

    @GetMapping("/bookings/new")
    public String newBooking(Model model) {
        // AG-CONSOLIDATION-001: Redirect to Unified Product Builder
        return "redirect:/products/new-products-form?type=BOOKING";
    }

    @PostMapping("/bookings/save")
    public String saveBooking(Booking booking, RedirectAttributes ra) {
        // AG-TENANT-FIX: Explicitly assign Tenant ID
        Long tenantId = com.onlineStoreCom.tenant.TenantContext.getTenantId();
        if (booking.getTenantId() == null) {
            booking.setTenantId(tenantId);
        }

        service.save(booking);
        ra.addFlashAttribute("message", "The booking has been saved successfully.");
        return "redirect:/bookings";
    }

    @GetMapping("/bookings/edit/{id}")
    public String editBooking(@PathVariable(name = "id") Long id, Model model, RedirectAttributes ra) {
        try {
            Booking booking = service.get(id);
            model.addAttribute("booking", booking);
            model.addAttribute("pageTitle", "Edit Booking (ID: " + id + ")");
            return "booking/booking_form";
        } catch (BookingNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
            return "redirect:/bookings";
        }
    }

    @GetMapping("/bookings/delete/{id}")
    public String deleteBooking(@PathVariable(name = "id") Long id, RedirectAttributes ra) {
        try {
            service.delete(id);
            ra.addFlashAttribute("message", "The booking ID " + id + " has been deleted successfully");
        } catch (BookingNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
        }
        return "redirect:/bookings";
    }
}
