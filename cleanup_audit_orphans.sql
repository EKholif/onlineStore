-- AG-DATA-REPAIR: Clean orphan audit log entries before Frontend startup
-- WHY: tenant_audit_log contains user_id references to deleted users
-- BUSINESS IMPACT: Prevents FK constraint creation failure during Hibernate schema update

DELETE
FROM tenant_audit_log
WHERE user_id IS NOT NULL
  AND user_id NOT IN (SELECT id FROM users);

SELECT CONCAT('Cleaned ', ROW_COUNT(), ' orphan audit log entries') AS result;
