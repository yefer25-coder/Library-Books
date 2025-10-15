# LibroNova - Library Management System (Java SE + JDBC + MVC)
##  Overview

LibroNova is a desktop application developed in Java SE (version 17+) that allows you to manage the catalog of books, users, members, and loans for a network of libraries.
The system uses JOptionPane for its graphical interface, JDBC (MySQL) for data persistence, and a modular layered architecture (Controller, Service, DAO, Model, View).

This solution replaces the manual handling of information in spreadsheets and physical forms, avoiding errors of duplication, inconsistency, and data loss.
It includes validations, custom exceptions, operation logs, and data export to CSV files.

---

## Main Features
### User Management and Authentication

Login with credential and role validation (ADMIN / ASSISTANT).

Decorator applied to the createUser() method to assign default values:

role = “ASSISTANT”

status = “ACTIVE”

createdAt = now()

Logging of “HTTP calls” in the console and app.log file.

### Book Management

Full CRUD (create, edit, list, delete).

Unique ISBN validation before registration.

Filters by author and category.

Display in formatted tables within JOptionPane.

### Member Management

Member registration, editing, and deletion.

Active status validation before loans are made.

### Loan Management

Register new loans with available stock validation.

Book returns with automatic calculation of fines and replacement of copies.

JDBC transaction handling (setAutoCommit(false), commit(), rollback()).

## Exports and Files

Export the entire catalog (libros_export.csv).

Export overdue loans (prestamos_vencidos.csv).

Read parameters from config.properties (DB, loan days, daily fine).

Log activity in app.log using java.util.logging.


---

```text
 java
 ┣ 📂 controllers     → Presentation logic management
 ┣ 📂 services        → Business rules, transactions, validations
 ┣ 📂 dao             → JDBC persistence
 |    ┣ 📂  impl      → Implementation of interfaces
 ┣ 📂 models          → Entity classes (Book, Loan, User, Member)
 ┣ 📂 views           → Interface with JOptionPane
 ┣ 📂 utils           → Helpers (logs, messages, validations, CSV)
 ┣ 📂 exceptions      → Custom exceptions
 ┗ 📄 App.java        → Main entry point

```
---

## Prerequisites


| Requirement | Recommended version |
| --------- | ------------------- |
| Java SE   | 17 or higher       |
| Maven     | 3.8+                |
| MySQL     | 8.0+                |


## Project Configuration
 Clone the repository


```bach
git clone https://github.com/usuario/libronova.git

```

### Create the MySQL database
```sql
CREATE DATABASE libronova;
USE libronova;
```
Then run the sql/schema.sql script included in the project to create the tables (books, users, members, loans).

### Configure the config.properties file

- Located in the project root (/src/main/resources/config.properties):
```text
db.url=jdbc:mysql://localhost:3306/libronova
db.user=root
db.password=****
loanDays=7
finePerDay=1500

```
---
### Use Case Diagram

<img width="461" height="596" alt="Image" src="https://github.com/user-attachments/assets/e92e3a9c-07f6-463e-bb36-b8bc221897d1" />

---
### Relational diagram

<img width="1121" height="498" alt="Image" src="https://github.com/user-attachments/assets/e20a26ed-97e2-4c88-a960-c3f997040858" />

---

## Criterios de Aceptación Cumplidos

### Requisito

- CRUD de libros con validación ISBN único	
- Filtros por autor/categoría	
- Login con roles (ADMIN/ASISTENTE)	
- CRUD de usuarios y socios	
- Préstamos y devoluciones con transacciones JDBC	
- Exportaciones CSV funcionales	
- Lectura de archivo .properties	
- Excepciones personalizadas	
- Logs “HTTP-like”	
- README + Diagramas	
---

# Autor

- **Nombre**: Yeferson Alejandro Garcia Marin
- **Clan**: Berners-Lee
- Proyecto desarrollado para la Prueba de Desempeño - Java SE + JDBC + MVC (LibroNova)























