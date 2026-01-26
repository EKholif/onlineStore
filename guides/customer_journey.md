# Customer Journey & Scenarios

## 1. Introduction
This guide outlines the standard end-user (customer) workflows on the **Multi-Tenant Storefront**. It validates how Entities, Themes, and the Tenant Protocol interact to deliver a seamless experience.

## 2. Scenario A: Browsing the Store
**Actor**: Anonymous Visitor
**Goal**: Find a product and view details.

### Workflow Steps
1.  **Landing Page**: Visitor hits `store.com`.
    -   *System*: `TenantContextFilter` identifies Tenant. `ThemeService` loads `ThemeDTO` (Primary Color, Logo).
    -   *View*: `index.html` renders using CSS variables from `theme-bootstrap.js`.
2.  **Navigation**: Clicks "Shop" in Menu.
    -   *System*: `CategoryService.listRoots()` fetches categories filtered by `tenant_id`.
3.  **Product List**: Views "Electronics".
    -   *System*: `ProductService.listByPage()` runs query with `@Filter(name="tenantFilter")`.
    -   *Result*: Only products for this tenant are shown.
4.  **Product Detail**: Clicks "Wireless Headphones".
    -   *System*: `ProductService.getProduct(alias)` fetches entity.
    -   *System*: `ReviewService` fetches related reviews (Tenant-isolated).

## 3. Scenario B: Registration & Login
**Actor**: New Customer
**Goal**: Create an account.

### Workflow Steps
1.  **Register**: Clicks "Register".
    -   *System*: `CustomerController` matches email uniqueness *within* this tenant only (`isEmailUnique` check).
2.  **Submit**: Fills form.
    -   *System*: Saves `Customer` entity with `tenant_id` automatically injected.
3.  **Login**: Enters credentials.
    -   *System*: `CustomerUserDetailsService` validates against Tenant + Email.

## 4. Scenario C: Purchase Flow (The "Happy Path")
**Actor**: Logged-in Customer
**Goal**: Buy items.

### Workflow Steps
1.  **Add to Cart**: Adds item.
    -   *System*: `CartItemService` saves `CartItem` linked to Customer + Product + Tenant.
2.  **Checkout**: Proceeds to checkout.
    -   *System*: `ShippingRateService` calculates cost based on Tenant's configured shipping rates.
3.  **Place Order**: Confirms payment (COD/Credit).
    -   *System*: Creates `Order` entity with `@Filter` protection.
    -   *System*: Moves `CartItem` data to `OrderDetail`.
    -   *System*: Clears Cart.
4.  **Confirmation**: Sees "Thank you".
    -   *Report*: `ReportManager` (async) logs "Order Placed" event for Sales Report.

## 5. Scenario D: Order Tracking
**Actor**: Customer
**Goal**: Check order status.

### Workflow Steps
1.  **My Orders**: Navigates to Profile > Orders.
    -   *System*: `OrderRepository.findByCustomer()` returns only this tenant's orders.
2.  **Details**: Views detailed status.
    -   *System*: `OrderTrack` history is displayed.

## 6. Technical Validation Points
- **Isolation**: At no point did the customer see a product, category, or order from another tenant.
- **Theming**: The entire journey respected the `ThemeDTO` colors.
- **Performance**: Filters were applied at the database level, ensuring efficient queries.
