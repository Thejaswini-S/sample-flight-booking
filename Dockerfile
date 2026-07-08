# --- Build stage: compile and package the boot jar ---
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# Copy the Gradle wrapper and build scripts first (better layer caching), then sources.
COPY gradlew ./
COPY gradle ./gradle
COPY settings.gradle build.gradle ./
COPY src ./src

RUN chmod +x ./gradlew && ./gradlew --no-daemon clean bootJar

# --- Run stage: slim JRE with just the jar ---
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]