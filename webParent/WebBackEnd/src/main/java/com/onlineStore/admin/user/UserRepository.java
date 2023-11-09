package com.onlineStore.admin.user;


import com.onlineStoreCom.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

 User findByEmail(String email);

 boolean existsByEmail(String email);

 public Long countById(Long id);

 @Query("UPDATE User u set  u.enable=?2 WHERE u.id = ?1 ")
 @Modifying

 public void enableUser (Long id, boolean enable);


}


