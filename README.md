# ⚙️ BagyGo - Backend API

BagyGo Backend is a robust, production-ready REST API developed with **Spring Boot 3**. It handles the core business logic, secure authentication, and payment processing for the BagyGo transport platform.

---

## 🚀 Key Features

- **Secure Authentication**: JWT-based stateless authentication with role-based access control (Admin, Expéditeur, Transporteur).
- **Comprehensive API**: RESTful endpoints for trip management, baggage requests, and monitoring.
- **Payment Integration**: Secure transaction handling with the **Stripe API**.
- **User Verification**: System for managing user profile approvals and verification documents.
- **Data Integrity**: Enforced via Spring Data JPA with a Repository-service-controller architecture.
- **Automated Seeding**: Built-in data initializer for quick development environment setup.

---

## 🛠 Tech Stack

- **Framework**: [Spring Boot 3.4.x](https://spring.io/projects/spring-boot)
- **Language**: Java 17
- **Database**: [MySQL 8](https://www.mysql.com/)
- **Security**: [Spring Security](https://spring.io/projects/spring-security) with JWT
- **ORM**: Spring Data JPA / Hibernate
- **Payments**: [Stripe SDK](https://stripe.com/docs/libraries/java)
- **Utilities**: Lombok, MapStruct

---

## 🏗 Architecture

The project follows a clean **MVC + Repository** architectural pattern:
- **Controllers**: Handle HTTP requests and define REST endpoints.
- **Services**: Contain business logic and orchestrate data flow.
- **Repositories**: Abstract database access using Spring Data JPA.
- **DTOs**: Data Transfer Objects for clean API request/response mapping.
- **Config**: Centralized security, CORS, and bean configurations.

---

## 📂 Project Structure

```text
src/main/java/com/bagygo/bagygo_backend/
├── config/             # Security, JWT, CORS, Stripe & Data Initializer
├── controller/         # REST Controllers (Auth, Trip, Request, Payment, Admin)
├── dto/                # Request & Response Data Transfer Objects
├── entity/             # JPA Entities (User, Trip, BaggageRequest, Payment, etc.)
├── repository/         # Spring Data JPA Repositories
├── service/           # Business Logic Interfaces and Implementations
└── enums/              # Shared enums (Role, Status, etc.)
```

---

## 📥 Installation & Setup

### Prerequisites
- [JDK 17](https://www.oracle.com/java/technologies/javase-jdk17-downloads.html)
- [Maven](https://maven.apache.org/)
- [MySQL](https://www.mysql.com/downloads/)

### 1. Database Setup
Create a MySQL database named `bagygo`:
```sql
CREATE DATABASE bagygo;
```

### 2. Configure Environment Variables
You can set these in your OS environment or create a `.env` file (or modify `application.properties` directly for local testing):

| Variable | Description |
| :--- | :--- |
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://localhost:3306/bagygo` |
| `DB_USERNAME` | Your MySQL username |
| `DB_PASSWORD` | Your MySQL password |
| `JWT_SECRET` | Secure key for token generation |
| `STRIPE_API_KEY` | Your Stripe secret key (sk_test_...) |

### 3. Build & Run
```bash
./mvnw clean install
./mvnw spring-boot:run
```
The API will be available at `http://localhost:8080`.

---

## 📡 API Endpoints (Summary)

### Authentication
- `POST /api/auth/register` - Create new account
- `POST /api/auth/login` - Authenticate and get JWT

### Management
- `GET/POST /api/trips` - Browse and post trips (Transporters)
- `GET/POST /api/requests` - Manage baggage requests (Senders)
- `POST /api/payments/create-checkout-session` - Initiate Stripe checkout

### Admin
- `GET /api/admin/users` - List and manage users
- `GET /api/admin/monitoring/**` - View system-wide metrics

---

## 📄 License

This project is proprietary. All rights reserved.

---

## 🤝 Contact
For any inquiries or technical support, please contact the repository owner.
