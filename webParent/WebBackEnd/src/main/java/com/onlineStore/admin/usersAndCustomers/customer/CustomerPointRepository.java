package com.onlineStore.admin.usersAndCustomers.customer;

import com.onlineStoreCom.entity.customer.Customer;
import com.onlineStoreCom.entity.customer.CustomerPoint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerPointRepository extends JpaRepository<CustomerPoint, Integer> {

    List<CustomerPoint> findByCustomer(Customer customer);

    Page<CustomerPoint> findByCustomer(Customer customer, Pageable pageable);

    List<CustomerPoint> findByCustomerOrderByTransactionDateDesc(Customer customer);
}
