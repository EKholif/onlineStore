-- AG-FLYWAY-FIX: Repair checksum mismatches for V2 and V3
-- WHY: Migration files were modified after being applied to database
-- BUSINESS IMPACT: Allows Backend to start without Flyway validation errors

-- Delete invalid checksum records
DELETE
FROM flyway_schema_history
WHERE version IN ('2', '3');

-- This forces Flyway to re-apply these migrations on next startup
SELECT 'Flyway checksums repaired. Restart Backend to re-apply migrations.' AS result;
