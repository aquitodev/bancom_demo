# 🏦 Bancom Microservice API

API REST para la gestión de usuarios y publicaciones con autenticación JWT. Sistema de microservicio desarrollado con Spring Boot que implementa autenticación segura, autorización basada en roles y operaciones CRUD completas.

## 📋 Tabla de Contenidos

- [Características](#-características)
- [Stack Tecnológico](#-stack-tecnológico)
- [Requisitos Previos](#-requisitos-previos)
- [Instalación](#-instalación)
- [Configuración](#️-configuración)
- [Comandos Principales](#-comandos-principales)
- [Docker](#-docker)
- [Endpoints de la API](#-endpoints-de-la-api)
- [Autenticación y Seguridad](#-autenticación-y-seguridad)
- [Testing](#-testing)
- [Estructura del Proyecto](#-estructura-del-proyecto)
- [Contribución](#-contribución)

---

## ✨ Características

- ✅ **Autenticación JWT** - Sistema de tokens seguros con expiración configurable
- ✅ **Autorización basada en roles** - Control de acceso granular con Spring Security
- ✅ **CRUD Completo** - Operaciones de usuarios y posts
- ✅ **Validaciones automáticas** - Bean Validation con DTOs
- ✅ **Soft Delete** - Desactivación lógica de registros
- ✅ **ArgumentResolver personalizado** - Inyección automática de `@AuthenticatedUserId`
- ✅ **Ownership Verification** - Solo los creadores pueden modificar sus posts
- ✅ **Manejo global de excepciones** - Respuestas de error consistentes
- ✅ **Base de datos en memoria** - H2 para desarrollo rápido
- ✅ **CORS configurado** - Listo para integración con frontend
- ✅ **API RESTful** - Diseño siguiendo buenas prácticas REST
- ✅ **Suite de tests completa** - +70 tests entre unitarios e integración

---

## 🛠️ Stack Tecnológico

### Core Framework
- **Java** 17 (LTS)
- **Spring Boot** 3.2.2
- **Spring Web** - Para construcción de APIs REST
- **Spring Data JPA** - Capa de persistencia
- **Spring Security** - Autenticación y autorización

### Seguridad
- **JWT (JSON Web Tokens)** - io.jsonwebtoken (jjwt) 0.12.3
- **HS256** - Algoritmo de firma para tokens

### Base de Datos
- **H2 Database** - Base de datos en memoria (desarrollo)
- **Hibernate** - ORM para mapeo objeto-relacional

### Validación
- **Jakarta Bean Validation** - Validaciones declarativas
- **Hibernate Validator** - Implementación de Bean Validation

### Testing
- **JUnit 5** - Framework de testing
- **Mockito** - Mocking framework
- **Spring Boot Test** - Testing utilities
- **MockMvc** - Testing de controllers
- **AssertJ / Hamcrest** - Assertions fluidas

### Herramientas de Desarrollo
- **Spring Boot DevTools** - Hot reload en desarrollo
- **Maven** - Gestión de dependencias y build
- **Spring Boot Actuator** - Monitoreo y métricas

---

## 📦 Requisitos Previos

Antes de comenzar, asegúrate de tener instalado:

- **Java JDK 17** o superior
- **Maven 3.6+** (o usar el wrapper incluido `./mvnw`)
- **Git** (opcional, para clonar el repositorio)

### Verificar instalación:

```bash
java -version    # Debe mostrar version 17 o superior
mvn -version     # Debe mostrar Maven 3.6 o superior
```

---

## 🚀 Instalación

### 1. Clonar el repositorio

```bash
git clone https://github.com/aquitodev/bancom_demo.git
cd bancom-ms
```

### 2. Dar permisos de ejecución al wrapper de Maven (macOS/Linux)

```bash
chmod +x mvnw
```

### 3. Compilar el proyecto

```bash
./mvnw clean install
```

---

## ⚙️ Configuración

### Variables de Entorno (Opcional)

El proyecto funciona con configuración por defecto, pero puedes personalizar:

```bash
# Puerto del servidor (default: 8005)
SERVER_PORT=8005

# Base de datos H2
SPRING_DATASOURCE_URL=jdbc:h2:mem:bancomdb
SPRING_DATASOURCE_USERNAME=usrdemo
SPRING_DATASOURCE_PASSWORD=demo

# JWT Secret Key (auto-generada en desarrollo)
JWT_SECRET_KEY=your-secret-key-here
JWT_EXPIRATION_TIME=3600000
```

### Archivo de configuración

Editar `src/main/resources/application.properties`:

```properties
spring.application.name=demo
server.port=8005

spring.datasource.url=jdbc:h2:mem:bancomdb
spring.datasource.username=usrdemo
spring.datasource.password=demo
spring.datasource.driver-class-name=org.h2.Driver

logging.level.org.hibernate.SQL=debug
```

---

## 🎯 Comandos Principales

### Desarrollo

```bash
# Compilar el proyecto (sin tests)
./mvnw clean compile

# Compilar con dependencias
./mvnw clean install

# Compilar sin ejecutar tests
./mvnw clean install -DskipTests

# Limpiar build artifacts
./mvnw clean
```

### Ejecución

```bash
# Iniciar la aplicación
./mvnw spring-boot:run

# Iniciar en un puerto específico
./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=8080

# Iniciar con perfil específico
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Build

```bash
# Generar JAR ejecutable
./mvnw clean package

# Ejecutar el JAR generado
java -jar target/demo-0.0.1-SNAPSHOT.jar

# Build con optimizaciones de producción
./mvnw clean package -Pprod
```

### Testing

```bash
# Ejecutar todos los tests
./mvnw test

# Ejecutar tests específicos
./mvnw test -Dtest=AuthControllerIntegrationTest

# Ejecutar tests con reporte de cobertura
./mvnw test jacoco:report

# Ejecutar solo tests unitarios
./mvnw test -Dgroups=unit

# Ejecutar solo tests de integración
./mvnw test -Dgroups=integration
```

### Mantenimiento

```bash
# Ver árbol de dependencias
./mvnw dependency:tree

# Actualizar dependencias
./mvnw versions:display-dependency-updates

# Formatear código
./mvnw spring-javaformat:apply

# Verificar versiones de plugins
./mvnw versions:display-plugin-updates
```

---

## � Docker

### Construcción y Despliegue con Docker

El proyecto incluye configuración completa de Docker siguiendo las mejores prácticas para Spring Boot:

#### Características del Dockerfile

- ✅ **Multi-stage build** - Separación de build y runtime para imágenes más pequeñas
- ✅ **Layered JARs** - Optimización de caché de Docker con capas de Spring Boot
- ✅ **Usuario no-root** - Seguridad mejorada ejecutando como usuario `spring`
- ✅ **JRE Alpine** - Imagen base mínima (~180MB vs ~500MB con JDK completo)
- ✅ **Health checks** - Monitoreo automático del estado del contenedor
- ✅ **JVM optimizado** - Configuración de memoria para contenedores

### Comandos Docker

#### Construcción de la Imagen

```bash
# Build básico
docker build -t bancom-api:latest .

# Build con etiqueta de versión
docker build -t bancom-api:0.0.1 -t bancom-api:latest .

# Build sin caché (force rebuild)
docker build --no-cache -t bancom-api:latest .

# Ver tamaño de la imagen
docker images bancom-api
```

#### Ejecutar Contenedor

```bash
# Ejecutar en modo interactivo
docker run -it --rm -p 8005:8005 --name bancom-service bancom-api:latest

# Ejecutar en segundo plano (detached)
docker run -d -p 8005:8005 --name bancom-service bancom-api:latest

# Con variables de entorno personalizadas
docker run -d -p 8080:8080 \
  -e SERVER_PORT=8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e JWT_SECRET_KEY=my-super-secret-key \
  --name bancom-service \
  bancom-api:latest

# Con límites de recursos
docker run -d -p 8005:8005 \
  --memory="512m" \
  --cpus="1.0" \
  --name bancom-service \
  bancom-api:latest
```

#### Gestión de Contenedores

```bash
# Ver logs del contenedor
docker logs bancom-service

# Logs en tiempo real
docker logs -f bancom-service

# Ver estado del contenedor
docker ps

# Inspeccionar contenedor
docker inspect bancom-service

# Ejecutar comandos dentro del contenedor
docker exec -it bancom-service sh

# Detener contenedor
docker stop bancom-service

# Iniciar contenedor detenido
docker start bancom-service

# Reiniciar contenedor
docker restart bancom-service

# Eliminar contenedor
docker rm -f bancom-service
```

### Docker Compose

Para un despliegue más completo con configuración predefinida:

```bash
# Iniciar todos los servicios
docker-compose up

# Iniciar en segundo plano
docker-compose up -d

# Ver logs
docker-compose logs -f

# Detener servicios
docker-compose down

# Detener y eliminar volúmenes
docker-compose down -v

# Reconstruir imágenes
docker-compose build

# Reconstruir y levantar
docker-compose up --build
```

### Variables de Entorno en Docker

El contenedor acepta las siguientes variables de entorno:

```bash
# Configuración del servidor
SERVER_PORT=8005                    # Puerto de la aplicación
SPRING_PROFILES_ACTIVE=prod        # Perfil de Spring Boot

# Base de datos
SPRING_DATASOURCE_URL=jdbc:h2:mem:bancomdb
SPRING_DATASOURCE_USERNAME=usrdemo
SPRING_DATASOURCE_PASSWORD=demo

# JWT
JWT_SECRET_KEY=your-secret-key      # Clave secreta para JWT
JWT_EXPIRATION_TIME=3600000         # Expiración en ms (1 hora)

# JVM
JAVA_OPTS=-Xms256m -Xmx512m        # Opciones de JVM
```

### Health Check

El contenedor incluye un health check automático:

```bash
# Ver estado de salud
docker inspect --format='{{json .State.Health}}' bancom-service | jq

# Health check manual
curl http://localhost:8005/actuator/health
```

### Optimizaciones de Producción

#### 1. Registry Push (Docker Hub / AWS ECR)

```bash
# Docker Hub
docker tag bancom-api:latest yourusername/bancom-api:latest
docker push yourusername/bancom-api:latest

# AWS ECR
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin your-account.dkr.ecr.us-east-1.amazonaws.com
docker tag bancom-api:latest your-account.dkr.ecr.us-east-1.amazonaws.com/bancom-api:latest
docker push your-account.dkr.ecr.us-east-1.amazonaws.com/bancom-api:latest
```

#### 2. Análisis de Seguridad

```bash
# Escanear vulnerabilidades con Trivy
docker run --rm -v /var/run/docker.sock:/var/run/docker.sock \
  aquasec/trivy image bancom-api:latest

# Escanear con Docker Scout
docker scout cves bancom-api:latest
```

#### 3. Optimización de Tamaño

```bash
# Ver capas de la imagen
docker history bancom-api:latest

# Analizar tamaño de capas
docker run --rm -v /var/run/docker.sock:/var/run/docker.sock \
  wagoodman/dive:latest bancom-api:latest
```

### Arquitectura Multi-Stage

El Dockerfile utiliza una arquitectura multi-stage optimizada:

```
┌─────────────────────────────────────┐
│   STAGE 1: Build Stage              │
│   - eclipse-temurin:17-jdk-alpine   │
│   - Maven build                     │
│   - Extract JAR layers              │
│   Tamaño: ~500MB (descartado)       │
└─────────────────┬───────────────────┘
                  │
                  ▼
┌─────────────────────────────────────┐
│   STAGE 2: Runtime Stage            │
│   - eclipse-temurin:17-jre-alpine   │
│   - Copy layered dependencies       │
│   - Non-root user                   │
│   Tamaño final: ~180MB              │
└─────────────────────────────────────┘
```

### Mejores Prácticas Implementadas

1. **Multi-stage build**: Reduce el tamaño final de la imagen
2. **Layered JARs**: Mejora el aprovechamiento de caché de Docker
3. **Usuario no-root**: Ejecuta como usuario `spring` para seguridad
4. **JRE en lugar de JDK**: Solo runtime, sin herramientas de desarrollo
5. **Alpine Linux**: Imagen base mínima
6. **Health checks**: Monitoreo automático del estado
7. **`.dockerignore`**: Excluye archivos innecesarios
8. **Variables de entorno**: Configuración flexible
9. **Resource limits**: Control de CPU y memoria
10. **Logging estructurado**: JSON con rotación automática

---

## �📡 Endpoints de la API

### Base URL
```
http://localhost:8005/api/v1
```

### 🔓 Endpoints Públicos (No requieren autenticación)

#### Autenticación

```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "nickname": "aquito1",
  "password": "123456"
}
```

**Respuesta exitosa (200):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "nickname": "aquito1"
}
```

#### Registro de Usuario

```http
POST /api/v1/usuario
Content-Type: application/json

{
  "cellphone": "0987654321",
  "name": "Juan",
  "lastname": "Pérez",
  "nickname": "juanp",
  "password": "password123"
}
```

**Respuesta exitosa (201):**
```json
{
  "mensaje": "El Usuario ha sido creado con éxito",
  "usuario": {
    "id": 1,
    "cellphone": "0987654321",
    "name": "Juan",
    "lastname": "Pérez",
    "nickname": "juanp",
    "active": true,
    "createAt": "2026-01-10T00:00:00.000+00:00"
  }
}
```

#### Listar Usuarios

```http
GET /api/v1/usuario/list
```

---

### 🔒 Endpoints Privados (Requieren autenticación)

**Header requerido:**
```
Authorization: Bearer {token}
```

#### Posts

**Listar todos los posts:**
```http
GET /api/v1/post/list
Authorization: Bearer {token}
```

**Crear post:**
```http
POST /api/v1/post
Authorization: Bearer {token}
Content-Type: application/json

{
  "text": "Este es mi nuevo post"
}
```

**Actualizar post (solo el creador):**
```http
PUT /api/v1/post/{id}
Authorization: Bearer {token}
Content-Type: application/json

{
  "text": "Post actualizado"
}
```

**Eliminar post:**
```http
DELETE /api/v1/post/{id}
Authorization: Bearer {token}
```

---

## 🔐 Autenticación y Seguridad

### Flujo de Autenticación

1. **Login**: Envía credenciales a `/api/v1/auth/login`
2. **Recibe Token**: Obtén el JWT en la respuesta
3. **Usa Token**: Incluye el token en el header `Authorization: Bearer {token}`
4. **Renovación**: El token expira en 1 hora por defecto

### Configuración de Seguridad

- **Algoritmo**: HS256 (HMAC con SHA-256)
- **Expiración**: 1 hora (3600000ms)
- **Stateless**: Sin sesiones en servidor
- **CORS**: Habilitado para todos los orígenes (desarrollo)

### Rutas Públicas vs Privadas

**Públicas:**
- `POST /api/v1/auth/login`
- `POST /api/v1/usuario`
- `GET /api/v1/usuario/list`

**Privadas (requieren JWT):**
- `GET /api/v1/post/list`
- `POST /api/v1/post`
- `PUT /api/v1/post/{id}`
- `DELETE /api/v1/post/{id}`

### Autorización por Ownership

Los posts solo pueden ser modificados por sus creadores. El sistema valida automáticamente usando `@AuthenticatedUserId`.

---

## 🧪 Testing

### Suite de Tests Implementada

- **73 tests totales**
- **~79% pasando** (58 tests)
- Cobertura de controllers, services, repositories y security

### Ejecutar Tests

```bash
# Todos los tests
./mvnw test

# Tests específicos de un componente
./mvnw test -Dtest=AuthControllerIntegrationTest
./mvnw test -Dtest=ServiceUsuarioImplTest
./mvnw test -Dtest=PostControllerIntegrationTest

# Ver resultados detallados
./mvnw test -X

# Con perfil de test
./mvnw test -Dspring.profiles.active=test
```

### Documentación de Tests

Para más detalles sobre la suite de tests, consulta [TEST_DOCUMENTATION.md](TEST_DOCUMENTATION.md)

---

## 📁 Estructura del Proyecto

```
bancom-ms/
├── src/
│   ├── main/
│   │   ├── java/com/bancom/app/demo/
│   │   │   ├── controller/          # Controllers REST
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── PostController.java
│   │   │   │   └── UsuarioController.java
│   │   │   ├── dto/                 # Data Transfer Objects
│   │   │   │   ├── LoginRequest.java
│   │   │   │   ├── PostCreacionRequest.java
│   │   │   │   └── UsuarioRegistroRequest.java
│   │   │   ├── entities/            # JPA Entities
│   │   │   │   ├── Post.java
│   │   │   │   └── Usuario.java
│   │   │   ├── repository/          # Spring Data Repositories
│   │   │   │   ├── PostRepository.java
│   │   │   │   └── UsuarioRepository.java
│   │   │   ├── service/             # Business Logic
│   │   │   │   ├── IServicePost.java
│   │   │   │   ├── IServiceUsuario.java
│   │   │   │   ├── ServicePostImpl.java
│   │   │   │   └── ServiceUsuarioImpl.java
│   │   │   ├── security/            # Security Configuration
│   │   │   │   ├── JwtTokenConfig.java
│   │   │   │   ├── SpringSecurityConfig.java
│   │   │   │   ├── SimpleGrantedAuthorityJsonCreator.java
│   │   │   │   ├── annotation/
│   │   │   │   │   └── AuthenticatedUserId.java
│   │   │   │   ├── filter/
│   │   │   │   │   └── JwtValidationFilter.java
│   │   │   │   └── resolver/
│   │   │   │       └── AuthenticatedUserIdResolver.java
│   │   │   ├── config/              # Application Configuration
│   │   │   │   └── WebMvcConfig.java
│   │   │   ├── exception/           # Exception Handling
│   │   │   │   └── GlobalExceptionHandler.java
│   │   │   └── BancomApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── import.sql           # Datos iniciales
│   └── test/                        # Tests
│       ├── java/com/bancom/app/demo/
│       │   ├── controller/          # Integration Tests
│       │   ├── service/             # Unit Tests
│       │   ├── repository/          # Repository Tests
│       │   └── security/            # Security Tests
│       └── resources/
│           └── application-test.properties
├── target/                          # Build output
├── Dockerfile                       # Docker configuration
├── docker-compose.yml               # Docker Compose configuration
├── .dockerignore                    # Docker ignore patterns
├── pom.xml                          # Maven configuration
├── README.md                        # Este archivo
├── TEST_DOCUMENTATION.md            # Documentación de tests
└── HELP.md                          # Ayuda de Spring Boot
```

---

## 🔄 Flujo de Trabajo Git

### Branches

- `master` - Rama principal de producción
- `develop` - Rama de desarrollo activa

### Workflow Recomendado

```bash
# Crear rama de feature
git checkout -b feature/nueva-funcionalidad

# Hacer commits
git add .
git commit -m "feat: descripción del cambio"

# Actualizar desde develop
git checkout develop
git pull origin develop

# Mergear feature
git merge feature/nueva-funcionalidad

# Push a develop
git push origin develop
```

---

## 📚 Recursos Adicionales

### Documentación

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/3.2.2/reference/html/)
- [Spring Security](https://docs.spring.io/spring-security/reference/)
- [JWT.io](https://jwt.io/) - Decodificar y verificar tokens JWT
- [Spring Data JPA](https://docs.spring.io/spring-data/jpa/reference/)

### Herramientas de Testing

- **Postman**: Colección de endpoints disponible
- **Swagger/OpenAPI**: Considerar agregar para documentación interactiva
- **H2 Console**: Habilitar en development para inspección de BD

---

## 🐛 Troubleshooting

### Problema: Puerto 8005 ya en uso

```bash
# Encontrar proceso usando el puerto
lsof -ti:8005

# Matar el proceso
kill -9 $(lsof -ti:8005)

# O cambiar el puerto
./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=8080
```

### Problema: Tests fallan

```bash
# Limpiar y reconstruir
./mvnw clean install

# Verificar Java version
java -version  # Debe ser 17+
```

### Problema: Maven wrapper sin permisos

```bash
chmod +x mvnw
```

---

## 👥 Contribución

### Guía de Contribución

1. Fork el repositorio
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

### Convención de Commits

Seguimos [Conventional Commits](https://www.conventionalcommits.org/):

- `feat:` Nueva funcionalidad
- `fix:` Corrección de bug
- `docs:` Cambios en documentación
- `test:` Añadir o modificar tests
- `refactor:` Refactorización de código
- `style:` Cambios de formato
- `chore:` Tareas de mantenimiento

---

## 📄 Licencia

Este proyecto está bajo la Licencia MIT - ver el archivo [LICENSE](LICENSE) para más detalles.

---

## 👤 Autor

**Alexander Quito**
- GitHub: [@aquitodev](https://github.com/aquitodev)
- Repository: [bancom_demo](https://github.com/aquitodev/bancom_demo)

---

## 📞 Soporte

Para reportar bugs o solicitar features, por favor abre un [issue](https://github.com/aquitodev/bancom_demo/issues) en GitHub.

---

**Última actualización**: Enero 2026  
**Versión**: 0.0.1-SNAPSHOT  
**Spring Boot**: 3.2.2  
**Java**: 17
