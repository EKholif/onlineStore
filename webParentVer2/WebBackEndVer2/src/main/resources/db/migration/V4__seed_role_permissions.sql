-- AG-RBAC-SEED-001: Initial Permission Seeding for Platform Admin, Agency Admin, Tenant Admin
-- 
-- Purpose:
-- - Define core permissions for the SaaS platform
-- - Map permissions to existing roles
-- - Enable strict RBAC from day one
--
-- Business Impact:
-- - Platform Admin: Full system control (manage tenants, impersonate, view all audit logs)
-- - Agency Admin: Manage own agency + child tenants (switch to children, manage child users)
-- - Tenant Admin: Manage own tenant only (no tenant switching, limited permissions)

-- Define Role IDs (assuming these are the IDs from existing roles table)
-- You may need to adjust these IDs based on your actual database
-- Role 1: Platform Admin
-- Role 2: Agency Admin  
-- Role 3: Tenant Admin

-- Platform Admin Permissions (Full System Access)
INSERT INTO role_permissions (role_id, permission, scope)
VALUES (1, 'MANAGE_TENANTS', 'ALL_TENANTS'),    -- Create/edit/delete any tenant
       (1, 'SWITCH_TENANT', 'ALL_TENANTS'),     -- Switch to any tenant
       (1, 'IMPERSONATE_USER', 'ALL_TENANTS'),  -- Impersonate any user
       (1, 'VIEW_AUDIT_LOGS', 'ALL_TENANTS'),   -- Access all audit logs
       (1, 'MANAGE_USERS', 'ALL_TENANTS'),      -- Manage users across all tenants
       (1, 'MANAGE_ROLES', 'PLATFORM'),         -- Create/edit roles (platform-wide)
       (1, 'MANAGE_PERMISSIONS', 'PLATFORM'),   -- Assign permissions to roles
       (1, 'VIEW_SYSTEM_SETTINGS', 'PLATFORM'), -- Access platform-level settings
       (1, 'MANAGE_HIERARCHY', 'ALL_TENANTS');
-- Modify tenant hierarchy

-- Agency Admin Permissions (Own Agency + Child Tenants)
INSERT INTO role_permissions (role_id, permission, scope)
VALUES (2, 'MANAGE_TENANTS', 'CHILD_TENANTS'),     -- Manage child tenants only
       (2, 'SWITCH_TENANT', 'OWN_AND_CHILDREN'),   -- Switch to own + child tenants
       (2, 'VIEW_AUDIT_LOGS', 'OWN_AND_CHILDREN'), -- View own + child audit logs
       (2, 'MANAGE_USERS', 'OWN_AND_CHILDREN'),    -- Manage users in own + child tenants
       (2, 'VIEW_REPORTS', 'OWN_AND_CHILDREN');
-- Access reports for own + children

-- Tenant Admin Permissions (Own Tenant Only - Most Restrictive)
INSERT INTO role_permissions (role_id, permission, scope)
VALUES (3, 'MANAGE_USERS', 'OWN_TENANT'),    -- Manage users in own tenant only
       (3, 'VIEW_AUDIT_LOGS', 'OWN_TENANT'), -- View own tenant audit logs
       (3, 'VIEW_REPORTS', 'OWN_TENANT'),    -- Access own tenant reports
       (3, 'MANAGE_PRODUCTS', 'OWN_TENANT'), -- Manage products in own tenant
       (3, 'MANAGE_ORDERS', 'OWN_TENANT');
-- Manage orders in own tenant

-- Note: Tenant Admin explicitly CANNOT switch tenants or impersonate users
