-- ============================================================================
-- AG-SEED-TEST-USERS-001: Create Hierarchical Test Users
-- ============================================================================
-- 
-- Creates 3 test users for multi-tenant hierarchy testing:
-- 1. platform@saas.com - Platform Admin (sees ALL tenants)
-- 2. agency@saas.com   - Agency Admin (sees agency + children)
-- 3. tenant@saas.com   - Tenant Admin (sees own tenant only)
--
-- Password for all: 123456 (BCrypt encoded)
-- ============================================================================

-- BCrypt hash for "123456"
SET @password_hash = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy';

-- ============================================================================
-- Step 1: Ensure Required Tenants Exist
-- ============================================================================

-- Tenant 0: SaaS Platform (Root)
INSERT IGNORE INTO tenants (id, name, code, status, created_at, active, level, parent_id)
VALUES (0, 'SaaS Platform', 'PLATFORM', 'ACTIVE', NOW(), 1, 0, NULL);

-- Tenant 1: Agency (Child of Platform)
INSERT IGNORE INTO tenants (id, name, code, status, created_at, active, level, parent_id)
VALUES (1, 'Demo Agency', 'AGENCY-01', 'ACTIVE', NOW(), 1, 1, 0);

-- Tenant 2: Store (Child of Agency)
INSERT IGNORE INTO tenants (id, name, code, status, created_at, active, level, parent_id)
VALUES (2, 'Demo Store', 'STORE-01', 'ACTIVE', NOW(), 1, 2, 1);

-- ============================================================================
-- Step 2: Ensure Required Roles Exist
-- ============================================================================

-- Role 1: Platform Admin (already exists, just ensure)
INSERT IGNORE INTO role (id, name, descrption)
VALUES (1, 'Platform Admin', 'Full platform access - manages all tenants');

-- Role 2: Agency Admin
INSERT IGNORE INTO role (id, name, descrption)
VALUES (2, 'Agency Admin', 'Agency-level access - manages agency and child tenants');

-- Role 3: Tenant Admin  
INSERT IGNORE INTO role (id, name, descrption)
VALUES (3, 'Tenant Admin', 'Tenant-level access - manages single tenant only');

-- ============================================================================
-- Step 3: Create Test Users
-- ============================================================================

-- Platform Admin User
INSERT INTO `'user'` (email, password, first_name, last_name, enabled, tenant_id)
SELECT 'platform@saas.com', @password_hash, 'Platform', 'Admin', 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `'user'` WHERE email = 'platform@saas.com');

SET @platform_id = (SELECT id
                    FROM `'user'`
                    WHERE email = 'platform@saas.com');

-- Agency Admin User
INSERT INTO `'user'` (email, password, first_name, last_name, enabled, tenant_id)
SELECT 'agency@saas.com', @password_hash, 'Agency', 'Admin', 1, 1
WHERE NOT EXISTS (SELECT 1 FROM `'user'` WHERE email = 'agency@saas.com');

SET @agency_id = (SELECT id
                  FROM `'user'`
                  WHERE email = 'agency@saas.com');

-- Tenant Admin User
INSERT INTO `'user'` (email, password, first_name, last_name, enabled, tenant_id)
SELECT 'tenant@saas.com', @password_hash, 'Tenant', 'Admin', 1, 2
WHERE NOT EXISTS (SELECT 1 FROM `'user'` WHERE email = 'tenant@saas.com');

SET @tenant_id = (SELECT id
                  FROM `'user'`
                  WHERE email = 'tenant@saas.com');

-- ============================================================================
-- Step 4: Assign Roles to Users
-- ============================================================================

-- Platform Admin gets Role 1
INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT @platform_id, 1
WHERE @platform_id IS NOT NULL;

-- Agency Admin gets Role 2
INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT @agency_id, 2
WHERE @agency_id IS NOT NULL;

-- Tenant Admin gets Role 3
INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT @tenant_id, 3
WHERE @tenant_id IS NOT NULL;

-- ============================================================================
-- Step 5: Link Users to Tenants (user_tenants - N:N relationship)
-- THIS IS THE CRITICAL STEP FOR ACCESS CONTROL!
-- ============================================================================

-- Platform Admin linked to Tenant 0 (gives access to ALL tenants)
INSERT IGNORE INTO user_tenants (user_id, tenant_id)
SELECT @platform_id, 0
WHERE @platform_id IS NOT NULL;

-- Agency Admin linked to Tenant 1 (agency level)
INSERT IGNORE INTO user_tenants (user_id, tenant_id)
SELECT @agency_id, 1
WHERE @agency_id IS NOT NULL;

-- Tenant Admin linked to Tenant 2 (store level only)
INSERT IGNORE INTO user_tenants (user_id, tenant_id)
SELECT @tenant_id, 2
WHERE @tenant_id IS NOT NULL;

-- ============================================================================
-- Step 6: Verification
-- ============================================================================

SELECT '=== Test Users Created ===' AS section;
SELECT u.id,
       u.email,
       u.first_name,
       u.tenant_id                AS primary_tenant,
       GROUP_CONCAT(ut.tenant_id) AS linked_tenants,
       GROUP_CONCAT(r.name)       AS roles
FROM `'user'` u
         LEFT JOIN user_tenants ut ON u.id = ut.user_id
         LEFT JOIN user_roles ur ON u.id = ur.user_id
         LEFT JOIN role r ON ur.role_id = r.id
WHERE u.email IN ('platform@saas.com', 'agency@saas.com', 'tenant@saas.com')
GROUP BY u.id, u.email, u.first_name, u.tenant_id;

SELECT '=== Tenant Hierarchy ===' AS section;
SELECT t.id,
       t.name,
       t.code,
       t.level,
       t.parent_id,
       (SELECT name FROM tenants WHERE id = t.parent_id) AS parent_name
FROM tenants t
WHERE t.id IN (0, 1, 2)
ORDER BY t.level;

-- ============================================================================
-- Test Credentials:
-- ============================================================================
-- platform@saas.com / 123456 → Platform Admin (sees ALL tenants)
-- agency@saas.com   / 123456 → Agency Admin (sees Tenant 1 + children)
-- tenant@saas.com   / 123456 → Tenant Admin (sees Tenant 2 only)
-- ============================================================================
