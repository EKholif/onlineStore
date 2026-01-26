package com.onlineStore.admin.usersAndCustomers.users;

import com.onlineStoreCom.entity.users.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.onlineStore.admin.repository.base.SearchableRepository;
import com.onlineStore.admin.repository.base.ToggleableRepository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>,
        ToggleableRepository<User, Integer>,
        SearchableRepository<User, Integer> {

    User findByEmail(String email);

    boolean existsByEmail(String email);

    Integer countById(Integer id);

    // Removed enableUser in favor of updateEnabledStatus
    // @Query("UPDATE User u set u.enabled=?2 WHERE u.id = ?1 ")
    // @Modifying
    // void enableUser(Integer id, boolean enable);

    @Override
    @Query("SELECT u FROM User u WHERE  CONCAT(u.id, ' ', u.email, ' ', u.firstName, ' '," +
            "u.lastName) LIKE %?1%")
    Page<User> findAll(String keyword, Pageable pageable);
}
