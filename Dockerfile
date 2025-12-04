# Use Eclipse Temurin JDK 21 as builder so Java 21 is available for toolchains
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /home/app
# Copy project files
COPY . .
# Use the gradle wrapper to build with the project's configured Gradle
RUN chmod +x ./gradlew && ./gradlew bootJar --no-daemon -x test

# Runtime image
FROM eclipse-temurin:21-jdk
LABEL authors="knalis"

# Copy built jar from builder
COPY --from=builder /home/app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]