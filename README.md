# LibroNova - Sistema de Gestión de Biblioteca (Java SE + JDBC + MVC)
##  Descripción General

LibroNova es una aplicación de escritorio desarrollada en Java SE (versión 17+) que permite gestionar el catálogo de libros, usuarios, socios y préstamos de una red de bibliotecas.
El sistema utiliza JOptionPane para su interfaz gráfica, JDBC (MySQL) para la persistencia de datos y una arquitectura modular por capas (Controller, Service, DAO, Model, View).

Esta solución reemplaza el manejo manual de información en hojas de cálculo y formularios físicos, evitando errores de duplicidad, inconsistencia y pérdida de datos.
Incluye validaciones, excepciones personalizadas, logs de operaciones y exportación de datos a archivos CSV.

---

## Características Principales
### Gestión de Usuarios y Autenticación

Inicio de sesión (login) con validación de credenciales y roles (ADMIN / ASISTENTE).

Decorador aplicado al método createUser() para asignar valores por defecto:

role = "ASISTENTE"

estado = "ACTIVO"

createdAt = now()

Registro de trazas tipo “llamadas HTTP” en consola y archivo app.log.

### Gestión de Libros

CRUD completo (crear, editar, listar, eliminar).

Validación de ISBN único antes de registrar.

Filtros por autor y categoría.

Visualización en tablas formateadas dentro de JOptionPane.

### Gestión de Socios

Alta, edición y eliminación de socios.

Validación de estado activo antes de realizar préstamos.

### Gestión de Préstamos

Registrar nuevos préstamos con validación de stock disponible.

Devolución de libros con cálculo automático de multa y reposición de ejemplares.

Manejo de transacciones JDBC (setAutoCommit(false), commit(), rollback()).

## Exportaciones y Archivos

Exportación del catálogo completo (libros_export.csv).

Exportación de préstamos vencidos (prestamos_vencidos.csv).

Lectura de parámetros desde config.properties (BD, días de préstamo, multa diaria).

Registro de actividad en app.log usando java.util.logging.


---

```text
📦 src
 ┣ 📂 controllers     → Manejo de la lógica de presentación
 ┣ 📂 services        → Reglas de negocio, transacciones, validaciones
 ┣ 📂 dao             → Persistencia JDBC
 ┣ 📂 models          → Clases de entidad (Book, Loan, User, Member)
 ┣ 📂 views           → Interfaz con JOptionPane
 ┣ 📂 utils           → Helpers (logs, mensajes, validaciones, CSV)
 ┗ 📄 App.java        → Punto de entrada principal

```
---

## Requsitos previos


| Requisito | Versión recomendada |
| --------- | ------------------- |
| Java SE   | 17 o superior       |
| Maven     | 3.8+                |
| MySQL     | 8.0+                |
| JUnit     | 5.x                 |

### Configuración del Proyecto


























