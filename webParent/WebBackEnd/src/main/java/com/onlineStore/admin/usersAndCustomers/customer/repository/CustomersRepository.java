package com.onlineStore.admin.usersAndCustomers.customer.repository;


import com.onlineStoreCom.entity.customer.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomersRepository extends JpaRepository<Customer, Integer> {

    @Query("SELECT c FROM Customer c WHERE c.email =?1")
    Customer findByEmail(String email);

    boolean existsByEmail(String email);

    Integer countById(Integer id);

    @Query("UPDATE Customer c set  c.enabled=?2 WHERE c.id = ?1 ")
    @Modifying
    void enableCustomer(Integer id, boolean enable);

    @Query("SELECT C FROM Customer C WHERE  CONCAT(C.id, ' ', C.email, ' ', C.firstName, ' '," +
            "C.lastName) LIKE %?1%")
    Page<Customer> findAll(String keyword, Pageable pageable);







}




