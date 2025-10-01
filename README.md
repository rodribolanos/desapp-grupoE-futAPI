# desapp-grupoE-futAPI

## Descripción del proyecto
Desapp-grupoE-futAPI es una API desarrollada para la gestión y extracción de información de equipos y jugadores de fútbol, utilizando técnicas de web scraping y procesamiento de datos. El proyecto está orientado a proveer endpoints REST para consultar datos de equipos, jugadores y sus estadísticas, integrando fuentes externas como WhoScored y otras páginas deportivas.

## Creadores
- Ian C.
- Grupo E - Universidad Nacional de Quilmes

## Funcionalidades principales
- Extracción de URLs de equipos y jugadores desde sitios web deportivos.
- Obtención de listas de jugadores y sus datos asociados.
- Procesamiento de páginas HTML estáticas y dinámicas mediante Selenium y Jsoup.
- Endpoints REST para consultar equipos, jugadores y estadísticas.
- Integración con bases de datos para persistencia de información.
- Configuración flexible para ejecutar pruebas con navegadores reales o HTML estático.

## Ejecución del proyecto

### Requisitos
- Java 21 (Eclipse Temurin recomendado)
- Gradle
- Docker (opcional, para despliegue y pruebas con Selenium)

### Ejecución local
1. Clonar el repositorio:
   ```bash
   git clone <url-del-repo>
   cd desapp-grupoE-futAPI
   ```
2. Compilar y ejecutar:
   ```bash
   ./gradlew build
   ./gradlew bootRun
   ```
3. Acceder a la API en `http://localhost:8080`

### Ejecución con Docker
1. Construir la imagen:
   ```bash
   docker build -t futapp .
   ```
2. Ejecutar el contenedor:
   ```bash
   docker run -p 8080:8080 futapp
   ```

### Ejecución de pruebas
- Ejecutar pruebas unitarias y de integración:
  ```bash
  ./gradlew test
  ```

## Variables de entorno
- `WEBDRIVER_URL`: Ruta hacia el Standalone Server de Selenium (por ejemplo, `http://localhost:4444/wd/hub`).
- `SPRING_PROFILES_ACTIVE`: Perfil de Spring Boot (`dev`, `test`, `prod`).

## Notas
- Para ejecutar scraping con Selenium en Docker, se recomienda usar imágenes como `selenium/standalone-chrome` y conectar la API vía WebDriver remoto.
- Para pruebas determinísticas, se puede configurar el proyecto para usar HTML estático y el navegador Jsoup.