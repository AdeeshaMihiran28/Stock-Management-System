# Web-Based Stock Management System

SE2030 Software Engineering module project built with Java 21, Spring Boot 3, Maven, Thymeleaf, Bootstrap 5, Spring Data JPA, Hibernate, Bean Validation, Spring Security, and MySQL.

## Features

- Product and Category Management
- Supplier Management
- Transactional Stock-In and Stock-Out
- Automatic Low Stock Alerts
- Reports and Dashboard
- Session-based Authentication and Role-Based Access Control
- Admin-only User Management with BCrypt password storage

## Prerequisites

- JDK 21 or newer (the project compiles for Java 21)
- MySQL 8 running on `localhost:3306`
- The `stock_management_db` database
- VS Code or another Java IDE (optional)

## Local setup

The application uses the local MySQL database `stock_management_db` on port `3306`. The username defaults to `root`; both it and the password can be supplied through environment variables.

In a VS Code PowerShell terminal, safely set the database password for the current terminal session:

```powershell
$secureDbPassword = Read-Host "MySQL password" -AsSecureString
$env:DB_PASSWORD = [System.Net.NetworkCredential]::new("", $secureDbPassword).Password
```

Optionally set a different username with `$env:DB_USERNAME = "your_username"`.

### Easy Windows start

Double-click `start-app.cmd` in the project folder. Enter the MySQL password when prompted. On the first run, also choose an initial admin password of at least 8 characters; after the admin exists, press Enter to skip that prompt. The launcher starts Spring Boot and opens the login page automatically. Close the terminal or press `Ctrl+C` to stop the application.

## Login and roles

The application uses session-based Spring Security login with BCrypt password storage. Available roles are `ADMIN`, `INVENTORY_MANAGER`, `STOCK_CLERK`, `PURCHASING_OFFICER`, and `MANAGEMENT`.

For the first run against a database with no users, set an initial administrator in the same PowerShell terminal. The password must contain at least 8 characters.

```powershell
$env:ADMIN_USERNAME = "admin"
$secureAdminPassword = Read-Host "Initial admin password" -AsSecureString
$env:ADMIN_PASSWORD = [System.Net.NetworkCredential]::new("", $secureAdminPassword).Password
$env:ADMIN_FULL_NAME = "System Administrator"
$env:ADMIN_EMAIL = ""
```

The initial administrator is created only when the `users` table is empty. After login, an administrator can create all other accounts from **Users**. No public registration is provided.

### Role access summary

| Role | Main access |
| --- | --- |
| `ADMIN` | All modules and User Management |
| `INVENTORY_MANAGER` | Categories, Products, Supplier viewing, Stock-In, Stock-Out, Low Stock, Reports |
| `STOCK_CLERK` | Product viewing, Stock-In, Stock-Out, Low Stock |
| `PURCHASING_OFFICER` | Product viewing, Suppliers, Stock-In, Low Stock, Reports |
| `MANAGEMENT` | Dashboard, read-only Products, Low Stock, Reports |

Run the application with the Maven wrapper:

```powershell
.\mvnw.cmd spring-boot:run
```

Then open <http://localhost:8080/login>.

## Main routes

| Route | Purpose |
| --- | --- |
| `/login` | Login |
| `/` | Dashboard |
| `/categories` | Categories |
| `/products` | Products |
| `/suppliers` | Suppliers |
| `/stock-in` | Stock-In |
| `/stock-out` | Stock-Out |
| `/low-stock` | Low Stock Alerts |
| `/reports` | Reports |
| `/users` | Admin-only User Management |

Build without starting the application:

```powershell
.\mvnw.cmd clean package
```
