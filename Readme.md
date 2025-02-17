# Shoppe - E-commerce Backend API

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

Shoppe is a RESTful API backend for an e-commerce platform, built with **Spring Boot**. It provides core functionalities for user authentication, product management, order processing, and shopping cart operations.

## Technologies Used

- **Java 17**
- **Spring Boot 3.2.x**
- **Spring Data JPA** (Database Interaction)
- **Spring Security** (Authentication & Authorization)
- **MySQL** / **H2 Database** (Production/Development)
- **Maven** (Build Tool)
- **JWT** (JSON Web Tokens for Authentication)
- **Lombok** (Boilerplate Reduction)
- **JUnit 5** & **Mockito** (Testing)

## Features

- **User Authentication & Authorization**
  - Register, login, and manage user profiles.
  - JWT-based secure API access.
  - Role-based access control (Admin/User).
- **Product Management**
  - CRUD operations for products and categories.
  - Search/filter products by name, category, price range, etc.
  - Pagination and sorting support.
- **Order Processing**
  - Create, view, and manage orders.
  - Shopping cart management (add/remove items).
- **Validation**
  - Input validation for APIs (e.g., email, password strength).

## Installation

### Prerequisites
- Java JDK 21
- MySQL 8.x (or H2 for development)
- Maven 3.8+

### Steps
1. **Clone the repository**:
   ```bash
   git clone https://github.com/mhieu100/shoppe-backend.git
   cd shoppe-backend
