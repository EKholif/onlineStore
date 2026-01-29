package com.onlineStore.admin.usersAndCustomers.users;

import com.onlineStoreCom.entity.users.User;
import com.onlineStoreCom.repo.base.BaseTenantRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends BaseTenantRepository<User, Integer> {

    @Query("SELECT u FROM User u WHERE u.email = ?1 AND u.tenantId = :#{T(com.onlineStoreCom.tenant.TenantContext).getTenantId()}")
    User findByEmail(String email);

    // AG-SEC-LOGIN-002: Global Search for Platform Login (Bypasses Tenant Filter)
    @Query(value = "SELECT * FROM users WHERE email = ?1", nativeQuery = true)
    User findByEmailGeneric(String email);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.email = ?1 AND u.tenantId = :#{T(com.onlineStoreCom.tenant.TenantContext).getTenantId()}")
    boolean existsByEmail(String email);

    @Query("SELECT count(u) FROM User u WHERE u.id = ?1 AND u.tenantId = :#{T(com.onlineStoreCom.tenant.TenantContext).getTenantId()}")
    Integer countById(Integer id);

    @Query("UPDATE User u set u.enabled=?2 WHERE u.id = ?1 AND u.tenantId = :#{T(com.onlineStoreCom.tenant.TenantContext).getTenantId()}")
    @Modifying
    void enableUser(Integer id, boolean enable);

    @Query("SELECT u FROM User u WHERE CONCAT(u.id, ' ', u.email, ' ', u.firstName, ' ', u.lastName) LIKE %?1% AND u.tenantId = :#{T(com.onlineStoreCom.tenant.TenantContext).getTenantId()}")
    Page<User> findAll(String keyword, Pageable pageable);
}
