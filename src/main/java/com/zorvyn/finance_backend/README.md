# Finance Data Processing Backend (Zorvyn Assignment)

## 🚀 Overview
This project is a backend system for managing financial records with role-based access control and dashboard analytics.

## 🛠 Tech Stack
- Java
- Spring Boot
- Spring Data JPA
- MySQL
- REST APIs

## 📌 Features

### 👤 User Management
- Create, update, delete users
- Role-based access (ADMIN, ANALYST, VIEWER)
- Active / Inactive status

### 💰 Financial Records
- Create, update, delete records
- Fields: amount, type, category, date, notes
- Linked with users

### 🔐 Access Control
- VIEWER: Read only
- ANALYST: Read + insights
- ADMIN: Full access

### 📊 Dashboard APIs
- Total Income
- Total Expense
- Net Balance
- Category-wise summary

## 🔗 API Endpoints

### Users
- POST /users
- GET /users
- GET /users/{id}
- PUT /users/{id}
- DELETE /users/{id}

### Records
- POST /records
- GET /records
- PUT /records/{id}
- DELETE /records/{id}

### Dashboard
- GET /dashboard/summary
- GET /dashboard/category-summary

## ⚙️ Setup

1. Clone repo
2. Create MySQL DB:
   ```sql
   CREATE DATABASE finance_db;