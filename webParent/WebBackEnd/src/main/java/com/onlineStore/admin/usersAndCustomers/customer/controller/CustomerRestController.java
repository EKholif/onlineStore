package com.onlineStore.admin.usersAndCustomers.customer.controller;

import com.onlineStore.admin.usersAndCustomers.customer.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerRestController {

    @Autowired
    private CustomerService service;

    @PostMapping("/customers/check_email")
    public String checkDuplicateEmail(@RequestParam(name = "id", required = false) Integer id,
                                      @RequestParam(name = "email") String email) {
        // AG-FIX: Use CustomerService (Tenant-aware) instead of UserService
        return service.isEmailUnique(id, email) ? "OK" : "Duplicated";
    }
}
