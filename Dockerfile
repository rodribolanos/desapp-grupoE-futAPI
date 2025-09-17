# ---------- Etapa 1: Build ----------
FROM gradle:8.9-jdk21 AS builder

# Carpeta de trabajo
WORKDIR /app

# Copy all the code to the container
COPY . .

# Compilamos el jar (sin correr tests para que sea más rápido en despliegue)
RUN ./gradlew clean build -x test

# ---------- Etapa 2: Runtime ----------
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copiamos el jar compilado desde la etapa anterior
COPY --from=builder /app/build/libs/*.jar app.jar

# Expone el puerto 8080 (cámbialo si usás otro)
EXPOSE 8080

# Comando de arranque
ENTRYPOINT ["java", "-jar", "app.jar"]