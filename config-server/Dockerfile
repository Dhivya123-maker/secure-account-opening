FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY . .
RUN chmod +x mvnw && ./mvnw clean package -DskipTests -pl config-server -am

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/config-server/target/*.jar app.jar
EXPOSE 8071
ENTRYPOINT ["java", "-jar", "app.jar"]
