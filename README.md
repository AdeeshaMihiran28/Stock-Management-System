# Web-Based Stock Management System

SE2030 Software Engineering Project built with Java 21, Spring Boot 3, Maven, Thymeleaf, Bootstrap, Spring Data JPA, Hibernate, Bean Validation, and MySQL.

## Local setup

The application uses the local MySQL database `stock_management_db` on port `3306`. The username defaults to `root`; both it and the password can be supplied through environment variables.

In a VS Code PowerShell terminal, set the password for the current terminal session:

```powershell
$env:DB_PASSWORD = "your_mysql_password"
```

Optionally set a different username with `$env:DB_USERNAME = "your_username"`.

Run the application with the Maven wrapper:

```powershell
.\mvnw.cmd spring-boot:run
```

Then open <http://localhost:8080>.

Build without starting the application:

```powershell
.\mvnw.cmd clean package
```
