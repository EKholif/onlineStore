package com.onlineStore.admin.usersAndCustomers.users.servcies;

import com.onlineStore.admin.UsernameNotFoundException;
import com.onlineStore.admin.usersAndCustomers.users.UserRepository;
import com.onlineStore.admin.usersAndCustomers.users.role.RoleRepository;
import com.onlineStoreCom.entity.users.Role;
import com.onlineStoreCom.entity.users.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional

public class UserService {

    public static final int USERS_PER_PAGE = 5;

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private RoleRepository roleRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private com.onlineStore.admin.utility.storage.StorageService storageService;

    // AG-REFACTOR-USER-003: Unified save method (Create/Update)
    public User saveUser(User user, org.springframework.web.multipart.MultipartFile multipartFile)
            throws java.io.IOException {
        LOGGER.info("Saving user: {}", user.getEmail());

        // 1. Handle Password
        if (user.getId() != null) {
            User existingUser = userRepo.findById(user.getId()).orElse(null);
            if (existingUser != null) {
                if (user.getPassword().isEmpty()) {
                    user.setPassword(existingUser.getPassword());
                } else {
                    encodePassword(user);
                }

                // Keep photos if not updating
                if (multipartFile.isEmpty()) {
                    user.setPhotos(existingUser.getPhotos());
                }
            } else {
                encodePassword(user); // Should not happen if ID exists but safety check
            }
        } else {
            encodePassword(user);
        }

        // 2. Handle Image
        if (!multipartFile.isEmpty()) {
            String fileName = org.springframework.util.StringUtils
                    .cleanPath(java.util.Objects.requireNonNull(multipartFile.getOriginalFilename()));
            user.setPhotos(fileName);

            User savedUser = userRepo.saveAndFlush(user);

            String uploadDir = storageService.getStoragePath(savedUser.getId(), "users");
            storageService.cleanDir(uploadDir);
            storageService.saveFile(uploadDir, fileName, multipartFile);

            LOGGER.debug("Saved user photo: {}", fileName);
            return savedUser;
        }

        return userRepo.saveAndFlush(user);
    }
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(UserService.class);

    public List<User> listAllUsers() {
        return userRepo.findAll();
    }

    public List<Role> listAllRoles() {
        return roleRepo.findAll();
    }

    public User saveUser(User user) {
        encodePassword(user);
        return userRepo.saveAndFlush(user);
    }
    // AG-CLEANUP: Removed saveUpdatededUser (redundant)

    public Page<User> listByPage(int pageNum, String sortField, String sortDir, String keyword) {
        Pageable pageable = createPageable(pageNum, sortField, sortDir);

        if (keyword != null) {
            return userRepo.findAll(keyword, pageable);
        }
        return userRepo.findAll(pageable);
    }

    private Pageable createPageable(int pageNum, String sortField, String sortDir) {
        Sort sort = "asc".equalsIgnoreCase(sortDir) ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        return PageRequest.of(pageNum - 1, USERS_PER_PAGE, sort);
    }

    private void encodePassword(User user) {

        String encodePassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodePassword);
    }

    public boolean isEmailUnique(Integer id, String email) {
        User userByEmail = userRepo.findByEmail(email);

        return userByEmail == null || userByEmail.getId().equals(id);
    }

    public User getUser(Integer id) throws UsernameNotFoundException {
        try {
            return userRepo.findById(id).get();

        } catch (NoSuchElementException ex) {

            throw new UsernameNotFoundException("Could not find any user with ID " + id);
        }
    }

    public Boolean existsById(Integer id) {
        return userRepo.findById(id).isPresent();
    }

    public void deleteUser(Integer id) throws UsernameNotFoundException {
        try {
            // AG-REFACTOR-USER-002: Cleanup artifacts
            if (userRepo.existsById(id)) {
                String storagePath = storageService.getStoragePath(id, "users");
                try {
                    storageService.deleteDir(storagePath);
                } catch (java.io.IOException e) {
                    LOGGER.error("Failed to delete user directory: {}", e.getMessage());
                }
            }
            userRepo.deleteById(id);

        } catch (NoSuchElementException ex) {

            throw new UsernameNotFoundException("Could not find any user with ID " + id);
        }
    }
    @Autowired
    private com.onlineStoreCom.repo.TenantRepository tenantRepo;
    @Autowired
    private com.onlineStore.admin.usersAndCustomers.users.UserTenantRepository userTenantRepo;

    // ============================================================================
    // AG-RBAC-ACCESS-001: Tenant Access Control Methods
    // ============================================================================

    public void UdpateUserEnableStatus(Integer id, Boolean enable) {
        userRepo.enableUser(id, enable);

    }

    public User getByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    /**
     * AG-RBAC-CAN-ACCESS-001: Check if user can access target tenant
     * <p>
     * Purpose:
     * - Validates tenant access based on hierarchy rules
     * - Called before tenant context switch
     * - Used in TenantContextFilter to prevent unauthorized access
     * <p>
     * Business Rules:
     * - Platform Admin (Tenant 0): Access ALL tenants
     * - Agency Admin: Access own + all children + N:N mapped tenants
     * - Tenant Admin: Access ONLY own tenant
     *
     * @param user           User attempting access
     * @param targetTenantId Tenant ID being accessed
     * @return true if access is allowed, false otherwise
     */
    public boolean canAccessTenant(User user, Long targetTenantId) {
        if (user == null || targetTenantId == null) {
            LOGGER.debug("canAccessTenant: user or targetTenantId is null");
            return false;
        }

        // AG-FIX-001: Collect tenant IDs from multiple sources (for robustness)
        java.util.Set<Long> userTenantIds = new java.util.HashSet<>();

        // Source 1: Try to get from N:N relationship (user.getTenants())
        var userTenants = user.getTenants();
        if (userTenants != null && !userTenants.isEmpty()) {
            for (var ut : userTenants) {
                if (ut.getTenant() != null) {
                    userTenantIds.add(ut.getTenant().getId());
                }
            }
            LOGGER.debug("canAccessTenant: Found {} tenants from N:N for user {}", userTenantIds.size(),
                    user.getEmail());
        }

        // Source 2: FALLBACK - Get from user_tenants table directly (if lazy load
        // returned nothing)
        if (userTenantIds.isEmpty() && user.getId() != null) {
            try {
                LOGGER.debug("canAccessTenant: Lazy load empty, trying direct DB query for user {}", user.getEmail());
                java.util.List<Long> directTenantIds = userTenantRepo.findTenantIdsByUserId(user.getId());
                if (directTenantIds != null && !directTenantIds.isEmpty()) {
                    userTenantIds.addAll(directTenantIds);
                    LOGGER.debug("canAccessTenant: Found {} tenants from direct query for user {}",
                            directTenantIds.size(), user.getEmail());
                } else {
                    LOGGER.debug("canAccessTenant: Direct DB query returned 0 tenants for user {}", user.getEmail());
                }
            } catch (Exception e) {
                LOGGER.warn("canAccessTenant: Failed to query user_tenants directly: {}", e.getMessage());
            }
        }

        // Source 3: ULTIMATE FALLBACK - Use user.getTenantId() (primary tenant from
        // user table)
        // AG-FIX-002: ALWAYS check primary tenant ID, regardless of N:N results.
        // This is crucial for Platform Admin (Tenant 0) who might not have N:N entries.
        Long primaryTenantId = user.getTenantId();
        LOGGER.debug("canAccessTenant: User {} Primary TenantID check: {}", user.getEmail(), primaryTenantId);

        if (primaryTenantId != null) {
            userTenantIds.add(primaryTenantId);
            LOGGER.debug("canAccessTenant: Added primary tenant {} for user {}", primaryTenantId, user.getEmail());
        }

        // If still empty, deny access
        if (userTenantIds.isEmpty()) {
            LOGGER.warn("canAccessTenant: No tenant association found for user {}", user.getEmail());
            return false;
        }

        LOGGER.debug("canAccessTenant: User {} has tenant IDs: {}, checking access to tenant {}",
                user.getEmail(), userTenantIds, targetTenantId);

        // Rule 1: Platform Admin (Tenant 0) can access ALL tenants
        if (userTenantIds.contains(0L)) {
            LOGGER.debug("canAccessTenant: User {} is Platform Admin (Tenant 0), granting access", user.getEmail());
            return true;
        }

        // Rule 2: User is directly associated with target tenant (including N:N)
        if (userTenantIds.contains(targetTenantId)) {
            LOGGER.debug("canAccessTenant: User {} has direct access to tenant {}", user.getEmail(), targetTenantId);
            return true;
        }

        // Rule 3: Agency Admin can access children of their tenants
        for (Long tenantId : userTenantIds) {
            java.util.List<com.onlineStoreCom.entity.tenant.Tenant> children = tenantRepo.findByParentId(tenantId);
            for (var child : children) {
                if (child.getId().equals(targetTenantId)) {
                    LOGGER.debug("canAccessTenant: User {} can access child tenant {} via parent {}",
                            user.getEmail(), targetTenantId, tenantId);
                    return true; // Target is a direct child
                }
            }
        }

        // Rule 4: No access
        LOGGER.warn("canAccessTenant: Access DENIED for user {} to tenant {}", user.getEmail(), targetTenantId);
        return false;
    }

    /**
     * AG-RBAC-GET-ACCESSIBLE-001: Get all tenants user can access
     * <p>
     * Purpose:
     * - Populates tenant switcher dropdown
     * - Returns only authorized tenants for the user
     * - Respects hierarchy rules
     * <p>
     * Business Impact:
     * - Prevents UI from showing unauthorized tenants
     * - Supports multi-tenant dashboards
     *
     * @param user User requesting accessible tenants
     * @return List of tenants user can access
     */
    public java.util.List<com.onlineStoreCom.entity.tenant.Tenant> getAccessibleTenants(User user) {
        java.util.List<com.onlineStoreCom.entity.tenant.Tenant> accessible = new java.util.ArrayList<>();

        if (user == null) {
            return accessible;
        }

        var userTenants = user.getTenants();
        if (userTenants == null || userTenants.isEmpty()) {
            return accessible;
        }

        // Extract user's tenant IDs
        java.util.Set<Long> userTenantIds = new java.util.HashSet<>();
        for (var ut : userTenants) {
            if (ut.getTenant() != null) {
                userTenantIds.add(ut.getTenant().getId());
                accessible.add(ut.getTenant()); // Add own tenants
            }
        }

        // Platform Admin sees ALL tenants
        if (userTenantIds.contains(0L)) {
            return tenantRepo.findAll();
        }

        // Agency Admin/Tenant Admin: Add all children
        java.util.Set<Long> addedIds = new java.util.HashSet<>(userTenantIds);
        for (Long tenantId : userTenantIds) {
            java.util.List<com.onlineStoreCom.entity.tenant.Tenant> children = tenantRepo.findByParentId(tenantId);
            for (var child : children) {
                if (!addedIds.contains(child.getId())) {
                    accessible.add(child);
                    addedIds.add(child.getId());
                }
            }
        }

        return accessible;
    }
}
