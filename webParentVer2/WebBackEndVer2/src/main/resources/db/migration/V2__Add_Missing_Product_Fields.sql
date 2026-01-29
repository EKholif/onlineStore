-- AG-PRODUCT-MIG-001: Add missing Product fields for Strict Compliance
-- 1. Inventory Management (Card 5)
-- 2. Location/Service Area (Card 7)

-- Check and Add stock_quantity
SET @dbname = DATABASE();
SET @tablename = "products";
SET @columnname = "stock_quantity";
SET @preparedStatement = (SELECT IF(
                                             (SELECT COUNT(*)
                                              FROM INFORMATION_SCHEMA.COLUMNS
                                              WHERE (table_name = @tablename)
                                                AND (table_schema = @dbname)
                                                AND (column_name = @columnname)) > 0,
                                             "SELECT 1",
                                             "ALTER TABLE products ADD COLUMN stock_quantity INT DEFAULT 0;"
                                     ));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- Check and Add location_address
SET @columnname = "location_address";
SET @preparedStatement = (SELECT IF(
                                             (SELECT COUNT(*)
                                              FROM INFORMATION_SCHEMA.COLUMNS
                                              WHERE (table_name = @tablename)
                                                AND (table_schema = @dbname)
                                                AND (column_name = @columnname)) > 0,
                                             "SELECT 1",
                                             "ALTER TABLE products ADD COLUMN location_address VARCHAR(512);"
                                     ));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- Check and Add location_latitude
SET @columnname = "location_latitude";
SET @preparedStatement = (SELECT IF(
                                             (SELECT COUNT(*)
                                              FROM INFORMATION_SCHEMA.COLUMNS
                                              WHERE (table_name = @tablename)
                                                AND (table_schema = @dbname)
                                                AND (column_name = @columnname)) > 0,
                                             "SELECT 1",
                                             "ALTER TABLE products ADD COLUMN location_latitude DOUBLE;"
                                     ));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- Check and Add location_longitude
SET @columnname = "location_longitude";
SET @preparedStatement = (SELECT IF(
                                             (SELECT COUNT(*)
                                              FROM INFORMATION_SCHEMA.COLUMNS
                                              WHERE (table_name = @tablename)
                                                AND (table_schema = @dbname)
                                                AND (column_name = @columnname)) > 0,
                                             "SELECT 1",
                                             "ALTER TABLE products ADD COLUMN location_longitude DOUBLE;"
                                     ));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;
