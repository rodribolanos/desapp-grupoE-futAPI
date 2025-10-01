# Stage 1: Build the application
FROM gradle:8.9-jdk21 AS build
WORKDIR /app
COPY build.gradle.kts settings.gradle.kts ./
COPY gradlew gradlew.bat ./
COPY gradle/wrapper/ ./gradle/wrapper/
COPY src ./src
RUN ./gradlew clean bootJar

# Stage 2: Create the final image
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/futapp-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]