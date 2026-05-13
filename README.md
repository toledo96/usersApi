# Users API

API REST para gestión de usuarios con roles, autenticación JWT y documentación con Swagger.  
Proyecto desarrollado con **Spring Boot 3.5.x** y **PostgreSQL**, pensado para prácticas profesionales y portafolio.

---

## 🚀 Tecnologías
- Java 17
- Spring Boot 3.5.x
- Spring Security + JWT
- JPA/Hibernate + PostgreSQL
- Swagger/OpenAPI
- Maven

---

## ⚙️ Instalación local

1. Clonar el repositorio:
   ```bash
   git clone https://github.com/tu-usuario/users-api.git
   cd users-api
   
## 🔐 Variables de entorno requeridas

    DB_URL=jdbc:postgresql://localhost:5433/users
    DB_USERNAME=postgres
    DB_PASSWORD=admin
    JWT_SECRET=mySuperSecretKeyForTests1234567890abcd
    ADMIN_USERNAME=admin
    ADMIN_PASSWORD=admin123


La aplicación necesita las siguientes variables:

DB_URL → URL JDBC de la base de datos (ejemplo: jdbc:postgresql://localhost:5433/users)

DB_USERNAME → Usuario de la base de datos

DB_PASSWORD → Contraseña de la base de datos

JWT_SECRET → Clave secreta para firmar los JWT

ADMIN_USERNAME → Usuario administrador inicial

ADMIN_PASSWORD → Contraseña del administrador inicial

    ./mvnw spring-boot:run



## 📌 Endpoints principales
    POST /api/v1/auth/login → Login y generación de JWT
    
    POST /api/v1/users → Crear usuario
    
    GET /api/v1/users → Listar usuarios
    
    DELETE /api/v1/users/{id} → Eliminar usuario
    
    PUT /api/v1/users/{id} → Actualizar usuario

## 📖 Documentación
Swagger UI disponible en:
http://localhost:8080/swagger-ui.html

## 🌐 Demo en Render

Una vez desplegado: https://tu-api.onrender.com/api/v1/users

## 🧪 Pruebas
El proyecto incluye pruebas unitarias con JUnit + Mockito y pruebas de controlador con MockMvc.
Ejecutar:

    ./mvnw test
