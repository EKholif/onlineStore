-- Create Platform Admin test user
-- Email: empt@admin.com
-- Password: 11

-- Step 1: Insert user
INSERT INTO `'user'` (email, password, first_name, last_name, enabled, tenant_id)
VALUES ('empt@admin.com',
        '$2a$10$xZQ7qY9nXqXJ5p5zZq5zZeYqXqXqXqXqXqXqXqXqXqXqXqXqXqXqX', -- BCrypt hash for "11"
        'Test',
        'Admin',
        1,
        0 -- Platform Admin belongs to tenant 0 (SaaS Master)
       );

-- Step 2: Get the user ID (assuming auto-increment)
SET @user_id = LAST_INSERT_ID();

-- Step 3: Assign Platform Admin role (role_id = 1)
INSERT INTO user_roles (user_id, role_id)
VALUES (@user_id, 1);

-- Step 4: Link user to tenant 0 in user_tenants
INSERT INTO user_tenants (user_id, tenant_id)
VALUES (@user_id, 0);

SELECT 'User created successfully!' AS status;
SELECT *
FROM `'user'`
WHERE email = 'empt@admin.com';
