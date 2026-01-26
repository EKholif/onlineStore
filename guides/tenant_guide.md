# Tenant & Admin User Guide

## 1. Introduction
Welcome to the **Multi-Tenant SaaS Platform**. This system is designed to provide strict isolation for your data while giving you powerful tools to manage your e-commerce store.

## 2. Access & Login
- **URL**: `/login` (Managed by strict Tenant Context).
- **Credentials**: Your email acts as your unique identifier within your tenant.
- **Security**: The system automatically detects your tenant environment. You cannot access data from other tenants.

## 3. Dashboard Overview
Upon login, you will see the Admin Dashboard.
- **Top Bar**: Search, Profile, and Settings.
- **Sidebar**: Access to all modules (Users, Catalog, Customers, Orders, Reports).

## 4. Managing Entities
All entities are strictly isolated.
### Catalog Management
- **Brands**: `Catalog > Brands`. Upload logos (isolated to your dedicated folder).
- **Categories**: `Catalog > Categories`. Create hierarchies.
- **Products**: `Catalog > Products`.
    - Images are stored in `/tenants/{id}/assets/products/`.
    - Pricing, inventory, and descriptions are private to you.

### Customer Management
- **Customers**: `Customers > Manage`.
- View registered customers.
- **Privacy**: You only see customers who registered on **your** storefront.

### Order Management
- **Orders**: `Orders > Manage`.
- Process orders, update status (Shipped, Delivered).
- **Traceability**: Every action is logged with your Tenant ID.

## 5. Reports System
The platform includes a robust reporting engine.
### Generating Reports
1. Navigate to `Reports`.
2. Select Report Type (Sales, Customers, Inventory).
3. Click **Generate**.
4. System creates:
    - **JSON**: Raw data for analysis.
    - **HTML**: Visual report for quick viewing.

### Accessing Reports
- Reports are stored in your secure folder: `/Reports/Sales/`, `/Reports/Inventory/`, etc.
- You can download past reports anytime.

## 6. Settings & Theming
### General Settings
- **Currency**: Set your store's currency.
- **Logo**: Upload your store logo.

### Frontend Theme
- **Navigate**: `Settings > Frontend Settings`.
- **Customize**:
    - Primary Color
    - Font Family
    - Header/Footer Styles
- **Live Preview**: See changes instantly before saving.
- **Note**: These changes affect **only** your public storefront. The Admin panel theme is separate.

## 7. Extensions
- If you need new Modules (e.g., Appointments, Forums), contact the Super Admin.
- All new extensions are guaranteed to be tenant-aware.
