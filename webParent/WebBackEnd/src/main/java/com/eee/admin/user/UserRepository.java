package com.eee.admin.user;


import com.eee.common.entity.Role;
import com.eee.common.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {


}