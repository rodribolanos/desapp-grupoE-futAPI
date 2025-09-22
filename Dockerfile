# ---------- Etapa 1: Build ----------
FROM gradle:8.9-jdk21 AS builder

# Carpeta de trabajo
WORKDIR /app

# Copy all the code to the container
COPY . .

# ✅ Give execute permissions to the Gradle wrapper
RUN chmod +x ./gradlew

# Compilamos el jar (sin correr tests para que sea más rápido en despliegue)
RUN ./gradlew clean build -x test

# ---------- Etapa 2: Runtime ----------
FROM eclipse-temurin:21-jre

WORKDIR /app

# Instala Google Chrome for Testing v139 y ChromeDriver v139
RUN apt-get update \
    && apt-get install -y wget unzip \
    && mkdir -p /opt/chrome139 \
    && wget -O /opt/chrome139/chromedriver-win64.zip "https://storage.googleapis.com/chrome-for-testing-public/139.0.7258.154/win64/chromedriver-win64.zip" \
    && unzip /opt/chrome139/chromedriver-win64.zip -d /opt/chrome139/ \
    && rm /opt/chrome139/chromedriver-win64.zip

ENV CHROME_BIN=/opt/chrome139/chromedriver-win64/chrome.exe
ENV CHROMEDRIVER_BIN=/opt/chrome139/chromedriver.exe

# Copiamos el jar compilado desde la etapa anterior
COPY --from=builder /app/build/libs/*.jar app.jar

# Expone el puerto 8080 (cámbialo si usás otro)
EXPOSE 8080

# Comando de arranque
ENTRYPOINT ["java", "-jar", "app.jar"]