package com.onlineStore.admin.usersAndCustomers.users.role;

import com.onlineStoreCom.entity.users.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findByName(String name);

}
