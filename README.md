# Finance Backend System

A Spring Boot backend project for managing users, financial records, and dashboard analytics with role-based access control.

## Overview
This project was built for a backend assessment focused on API design, business logic, access control, validation, persistence, and dashboard-level summary APIs.

## Tech Stack
- Java
- Spring Boot
- Spring Data JPA
- MySQL
- Lombok
- Swagger / OpenAPI

## Features
- User management
- Role-based access control
- Financial records CRUD
- Record filtering by category, type, and date range
- Dashboard summary APIs
- Soft delete for users and financial records
- Global exception handling
- Swagger API documentation

## Role Access
- **VIEWER**: can access dashboard APIs
- **ANALYST**: can read records and access dashboard APIs
- **ADMIN**: can manage users and financial records and access dashboard APIs

## Main APIs
### User APIs
- `POST /users`
- `GET /users`
- `GET /users/{id}`
- `PUT /users/{id}`
- `PATCH /users/{id}/status`
- `DELETE /users/{id}`

### Financial Record APIs
- `POST /records`
- `GET /records`
- `GET /records/{id}`
- `PUT /records/{id}`
- `DELETE /records/{id}`
- `GET /records/filter`

### Dashboard APIs
- `GET /dashboard/summary`
- `GET /dashboard/total-income`
- `GET /dashboard/total-expense`
- `GET /dashboard/net-balance`
- `GET /dashboard/category-summary`
- `GET /dashboard/recent-activity`
- `GET /dashboard/monthly-trends`

## How to Run
1. Clone the repository
2. Create the MySQL database
3. Update database credentials in `src/main/resources/application.properties`
4. Run the application
5. Open Swagger UI

## Swagger URL
- `http://localhost:8080/swagger-ui/index.html`

## Important Assumption
This project uses **mock role-based authorization** by passing `role` as a request parameter. JWT or Spring Security based authentication is not implemented in this version.

## Soft Delete
Soft delete is implemented for:
- Users
- Financial records

## Future Improvements
- JWT authentication with Spring Security
- Pagination for listing APIs
- Unit and integration tests
- Docker deployment
