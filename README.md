# OnlineStore

A complete shopping website developed using Java, Spring Boot, Thymeleaf, Bootstrap, jQuery, and MySQL. The project includes both a customer-facing shopping application and an admin application for managing users, products, and orders.

## Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Installation](#installation)
- [Configuration](#configuration)
- [Usage](#usage)
- [API Endpoints](#api-endpoints)
- [Testing](#testing)
- [Contributing](#contributing)
- [License](#license)
- [Contact](#contact)

---

## Features

### Shopping Application
- Browse products, add to cart, checkout.
- Payment integration via PayPal and credit cards.
- User registration, authentication, and profile management.
- Order history and tracking.

### Admin Application
- Manage users, products, and orders.
- Dashboard for analytics and reporting.
- Role-based access control.

### Technical Features
- Spring Data JPA with Hibernate ORM.
- Authentication via Spring Security.
- Dynamic views using Thymeleaf template engine.
- RESTful web services (Spring REST).
- Payments integrated with PayPal Checkout API.
- Responsive UI with Bootstrap and jQuery.
- Unit and integration tests with JUnit, Mockito, and others.

---

## Tech Stack

- **Backend:** Java, Spring Boot, Spring Data JPA, Hibernate, Spring Security, Spring REST
- **Frontend:** Thymeleaf, Bootstrap, jQuery
- **Database:** MySQL
- **Payment Integration:** PayPal Checkout API, credit card processing (e.g., Stripe, if applicable)
- **Testing:** JUnit, Mockito

---

## Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/EKholif/onlineStore.git
   cd onlineStore
   ```
2. **Configure MySQL Database**
   - Create a database named `onlinestore`.
   - Update `src/main/resources/application.properties` with your MySQL credentials:
     ```
     spring.datasource.url=jdbc:mysql://localhost:3306/onlinestore
     spring.datasource.username=YOUR_USERNAME
     spring.datasource.password=YOUR_PASSWORD
     ```
3. **Set up PayPal and other payment credentials**
   - Update payment configuration in `application.properties` or relevant config files.

4. **Build and run the project**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

5. **Access the application**
   - Customer site: `http://localhost:8080/`
   - Admin site: `http://localhost:8080/admin`

---

## Configuration

- **Environment Variables:**
  - Database credentials
  - PayPal API credentials
  - Other payment gateway credentials

- **application.properties**
  - Configure mail, payment, and other environment-specific settings.

---

## Usage

- Register an account or log in as a user.
- Browse products, add to cart, and checkout.
- For admin, log in to the `/admin` panel to manage products, users, and orders.

---

## API Endpoints

### Product API
- `GET /api/products` - list all products
- `POST /api/products` - add a product (admin only)
- `PUT /api/products/{id}` - update a product (admin only)
- `DELETE /api/products/{id}` - delete a product (admin only)

### Order API
- `GET /api/orders` - list user orders
- `POST /api/orders` - place an order

### User API
- `GET /api/users` - list users (admin)
- `POST /api/users` - register user

_(Expand with more details as needed)_

---

## Testing

- Unit tests with JUnit
- Integration tests with JUnit and Mockito
- To run tests:
  ```bash
  mvn test
  ```

---

## Contributing

Contributions are welcome! Please follow these steps:
1. Fork the repository.
2. Create your feature branch: `git checkout -b feature/your-feature`
3. Commit your changes: `git commit -am 'Add new feature'`
4. Push to the branch: `git push origin feature/your-feature`
5. Create a pull request.

---

## License

MIT License. See [LICENSE](LICENSE) for details.

---

## Contact

For support or questions, open an issue or contact [EKholif](https://github.com/EKholif).
