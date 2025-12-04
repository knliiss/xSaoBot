FROM gradle:8.6-jdk AS builder
WORKDIR /home/gradle/project
COPY --chown=gradle:gradle . .
RUN gradle bootJar --no-daemon -x test

# Runtime image
FROM eclipse-temurin:21-jdk
LABEL authors="knalis"

COPY --from=builder /home/gradle/project/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]