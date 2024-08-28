package com.onlineStore.admin.user;


import com.onlineStoreCom.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

    boolean existsByEmail(String email);

    Long countById(Long id);

    @Query("UPDATE User u set  u.enable=?2 WHERE u.id = ?1 ")
    @Modifying
    void enableUser(Long id, boolean enable);

    @Query("SELECT u FROM User u WHERE  CONCAT(u.id, ' ', u.email, ' ', u.firstName, ' '," +
            "u.lastName) LIKE %?1%")
    Page<User> findAll(String keyword, Pageable pageable);
}




