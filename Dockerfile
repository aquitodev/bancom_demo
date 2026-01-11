# ==============================================
# STAGE 1: Build Stage
# ==============================================
FROM eclipse-temurin:17-jdk-alpine AS build

# Establecer directorio de trabajo
WORKDIR /app

# Copiar archivos de configuración de Maven
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Dar permisos de ejecución al wrapper
RUN chmod +x mvnw

# Descargar dependencias (capa cacheada)
RUN ./mvnw dependency:go-offline -B

# Copiar código fuente
COPY src src

# Compilar y empaquetar la aplicación
# -DskipTests: Omitir tests para build más rápido en producción
# -Dmaven.test.skip=true: Alternativa más agresiva
RUN ./mvnw clean package -DskipTests -B

# Extraer las capas del JAR para optimizar el caching de Docker
RUN mkdir -p target/dependency && \
    cd target/dependency && \
    java -Djarmode=layertools -jar ../*.jar extract

# ==============================================
# STAGE 2: Runtime Stage
# ==============================================
FROM eclipse-temurin:17-jre-alpine AS runtime

# Metadatos de la imagen
LABEL maintainer="aquitodev@github.com"
LABEL description="Bancom Microservice API - Spring Boot Application"
LABEL version="0.0.1-SNAPSHOT"

# Argumentos configurables
ARG APP_USER=spring
ARG APP_GROUP=spring
ARG APP_HOME=/app

# Instalar dependencias necesarias y crear usuario no-root
RUN apk add --no-cache \
    curl \
    tzdata && \
    addgroup -S ${APP_GROUP} && \
    adduser -S ${APP_USER} -G ${APP_GROUP}

# Establecer directorio de trabajo
WORKDIR ${APP_HOME}

# Copiar las capas extraídas desde el build stage
# Orden optimizado para mejor aprovechamiento de caché de Docker
COPY --from=build --chown=${APP_USER}:${APP_GROUP} /app/target/dependency/dependencies/ ./
COPY --from=build --chown=${APP_USER}:${APP_GROUP} /app/target/dependency/spring-boot-loader/ ./
COPY --from=build --chown=${APP_USER}:${APP_GROUP} /app/target/dependency/snapshot-dependencies/ ./
COPY --from=build --chown=${APP_USER}:${APP_GROUP} /app/target/dependency/application/ ./

# Cambiar a usuario no-root por seguridad
USER ${APP_USER}:${APP_GROUP}

# Variables de entorno por defecto
ENV SERVER_PORT=8005 \
    SPRING_PROFILES_ACTIVE=prod \
    JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Exponer puerto de la aplicación
EXPOSE ${SERVER_PORT}

# Healthcheck para monitoreo
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:${SERVER_PORT}/actuator/health || exit 1

# Comando de inicio optimizado
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} org.springframework.boot.loader.launch.JarLauncher"]
