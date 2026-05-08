# ============================================================
# Dockerfile - ms-pacientes
# Build multi-etapa para imagen optimizada y segura
# ============================================================

# --- Etapa 1: Build con Maven ---
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder

WORKDIR /app

# Copiar dependencias primero (cache de Docker)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar código fuente y compilar
COPY src ./src
RUN mvn clean package -DskipTests -B

# --- Etapa 2: Imagen de runtime mínima ---
FROM eclipse-temurin:21-jre-alpine

LABEL maintainer="hospital-sistemas@edu.ec"
LABEL description="Microservicio de Gestión de Pacientes"
LABEL version="1.0.0"

WORKDIR /app

# Usuario no root para seguridad
RUN addgroup -S hospital && adduser -S pacientes -G hospital

# Copiar solo el JAR desde el builder
COPY --from=builder /app/target/ms-pacientes-1.0.0.jar app.jar

# Crear directorio de logs con permisos correctos
RUN mkdir -p /app/logs && chown -R pacientes:hospital /app/logs

USER pacientes

# Puerto expuesto
EXPOSE 8081

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider \
    http://localhost:8081/api/actuator/health || exit 1

# Variables de entorno con valores por defecto
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC"
ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
