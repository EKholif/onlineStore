-- AG-SCHEMA-FIX-001: Foreign Key Constraint Violation Fix
-- Purpose: Clean orphaned user_tenants records before re-enabling FK constraints

USE shop;

-- Step 1: Find orphaned user_tenants (users that don't exist)
SELECT 'Orphaned user_tenants records:' AS info;
SELECT ut.id, ut.user_id, ut.tenant_id
FROM user_tenants ut
         LEFT JOIN users u ON ut.user_id = u.id
WHERE u.id IS NULL
LIMIT 50;

-- Step 2: Count total orphaned records
SELECT COUNT(*) as total_orphaned_records
FROM user_tenants ut
         LEFT JOIN users u ON ut.user_id = u.id
WHERE u.id IS NULL;

-- Step 3: Drop existing FK constraints (ignore errors if not exists)
SET FOREIGN_KEY_CHECKS = 0;

-- Drop the problematic constraint (will error if doesn't exist, but we ignore it)
-- We'll use a procedure to handle this gracefully
DROP PROCEDURE IF EXISTS drop_fk_if_exists;

DELIMITER $$
CREATE PROCEDURE drop_fk_if_exists()
BEGIN
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION BEGIN
    END;
    ALTER TABLE user_tenants
        DROP FOREIGN KEY FK9al929m2h3hecov7100p06cll;
    ALTER TABLE user_tenants
        DROP FOREIGN KEY FKuser_tenants_user_id;
END$$
DELIMITER ;

CALL drop_fk_if_exists();
DROP PROCEDURE drop_fk_if_exists;

-- Step 4: Delete orphaned records
DELETE ut
FROM user_tenants ut
         LEFT JOIN users u ON ut.user_id = u.id
WHERE u.id IS NULL;

SELECT 'Deleted orphaned records' AS info, ROW_COUNT() AS deleted_count;

-- Step 5: Re-create FK constraint properly
ALTER TABLE user_tenants
    ADD CONSTRAINT FKuser_tenants_user_id
        FOREIGN KEY (user_id) REFERENCES users (id)
            ON DELETE CASCADE;

-- Step 6: Verify no orphans remain
SELECT 'Verification - orphaned records after cleanup:' AS info;
SELECT COUNT(*) as remaining_orphans
FROM user_tenants ut
         LEFT JOIN users u ON ut.user_id = u.id
WHERE u.id IS NULL;

SET FOREIGN_KEY_CHECKS = 1;

SELECT 'Fix completed successfully!' AS status;
