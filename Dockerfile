# Stage 1: Build
FROM gradle:8.11.1-jdk21
WORKDIR /app
COPY build.gradle settings.gradle gradle.properties ./
COPY gradle ./gradle
COPY src ./src
RUN gradle bootJar --no-daemon

# Stage 2: Run
FROM eclipse-temurin:20-jdk-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]