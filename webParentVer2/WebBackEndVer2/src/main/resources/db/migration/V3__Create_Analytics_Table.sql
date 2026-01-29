-- AG-ANALYTICS-MIG-001: Create Daily Product Stats Table
-- Separation of Concerns: Analytics data stored separately from Product data to prevent locking.

CREATE TABLE IF NOT EXISTS `daily_product_stats`
(
    `id`             BIGINT NOT NULL AUTO_INCREMENT,
    `date`           DATE   NOT NULL,
    `product_id`     INT    NOT NULL,
    `tenant_id`      BIGINT NOT NULL,
    `view_count`     BIGINT DEFAULT 0,
    `cart_add_count` BIGINT DEFAULT 0,
    `sales_count`    BIGINT DEFAULT 0,
    `revenue`        DOUBLE DEFAULT 0.0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_date_product_tenant` (`date`, `product_id`, `tenant_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- Index for fast retrieval by product and date
CREATE INDEX `idx_stats_product_date` ON `daily_product_stats` (`product_id`, `date`);
