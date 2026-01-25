-- ============================================================================
-- AG-FIX-PLATFORM-ADMIN-001: Fix Platform Admin Access to Tenant 0
-- ============================================================================
-- 
-- Problem: root@saasmaster.com gets "Access denied to tenant 0"
-- Cause: User is not linked in user_tenants table (N:N relationship)
-- Solution: Add entry in user_tenants linking user to tenant 0
--
-- Run this script in MySQL to fix the issue
-- ============================================================================

-- Step 1: Ensure Tenant 0 (SaaS Master) exists
INSERT IGNORE INTO tenants (id, name, code, status, created_at, active, level)
VALUES (0, 'SaaS Platform', 'PLATFORM', 'ACTIVE', NOW(), 1, 0);

-- Step 2: Get the user ID for root@saasmaster.com
SET @platform_user_id = (SELECT id
                         FROM `'user'`
                         WHERE email = 'root@saasmaster.com');

-- Step 3: Check if user exists
SELECT CASE
           WHEN @platform_user_id IS NULL THEN 'ERROR: User root@saasmaster.com not found!'
           ELSE CONCAT('Found user ID: ', @platform_user_id)
           END AS status;

-- Step 4: Add user to user_tenants if not already linked
INSERT IGNORE INTO user_tenants (user_id, tenant_id)
SELECT @platform_user_id, 0
WHERE @platform_user_id IS NOT NULL
  AND NOT EXISTS (SELECT 1
                  FROM user_tenants
                  WHERE user_id = @platform_user_id
                    AND tenant_id = 0);

-- Step 5: Verify the fix
SELECT 'Verification:' AS section;
SELECT u.id,
       u.email,
       u.first_name,
       u.tenant_id  AS primary_tenant,
       ut.tenant_id AS linked_tenant
FROM `'user'` u
         LEFT JOIN user_tenants ut ON u.id = ut.user_id
WHERE u.email = 'root@saasmaster.com';

-- Step 6: Show all users linked to tenant 0 (Platform Admins)
SELECT '--- Platform Admins (Tenant 0) ---' AS section;
SELECT u.id, u.email, u.first_name, u.last_name
FROM `'user'` u
         INNER JOIN user_tenants ut ON u.id = ut.user_id
WHERE ut.tenant_id = 0;

-- ============================================================================
-- After running this, restart the application and try logging in again
-- ============================================================================
