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

# ---------- Etapa 2: JRE base (para copiar el runtime Java) ----------
FROM eclipse-temurin:21-jre AS jre

# ---------- Etapa 3: Runtime ----------
FROM debian:bookworm-slim

WORKDIR /app

# Instala Firefox (ESR) y dependencias en Debian + descarga GeckoDriver
RUN apt-get update \
    && apt-get install -y --no-install-recommends \
       ca-certificates wget curl tar unzip \
       firefox-esr fonts-liberation \
       libnss3 libatk-bridge2.0-0 libgtk-3-0 libx11-xcb1 libxcomposite1 libxdamage1 libxrandr2 libgbm1 libasound2 libpangocairo-1.0-0 libatspi2.0-0 libdrm2 libxshmfence1 libxss1 libxtst6 \
    && rm -rf /var/lib/apt/lists/* \
    # Descargar e instalar GeckoDriver
    && GECKO_VERSION=0.35.0 \
    && wget -O /tmp/geckodriver.tar.gz "https://github.com/mozilla/geckodriver/releases/download/v${GECKO_VERSION}/geckodriver-v${GECKO_VERSION}-linux64.tar.gz" \
    && tar -xzf /tmp/geckodriver.tar.gz -C /usr/local/bin \
    && chmod +x /usr/local/bin/geckodriver \
    && rm -f /tmp/geckodriver.tar.gz

# Copia el JRE de Temurin desde la etapa intermedia
COPY --from=jre /opt/java/openjdk /opt/java/openjdk
ENV JAVA_HOME=/opt/java/openjdk
ENV PATH="$JAVA_HOME/bin:$PATH"

# Variables usadas por el código Selenium
ENV GECKO_DRIVER_PATH=/usr/local/bin/geckodriver
ENV FIREFOX_BIN=/usr/bin/firefox-esr

# Copiamos el jar ejecutable de Spring Boot desde la etapa de build
COPY --from=builder /app/build/libs/futapp-0.0.1-SNAPSHOT.jar app.jar

# Expone el puerto 8080 (cámbialo si usás otro)
EXPOSE 8080

# Comando de arranque
ENTRYPOINT ["java", "-jar", "app.jar"]