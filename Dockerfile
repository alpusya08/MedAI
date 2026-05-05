# Stage 1: build
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -q
COPY src ./src
RUN mvn package -DskipTests -q

# Stage 2: run
FROM eclipse-temurin:17-jre
WORKDIR /app
RUN groupadd -r medai && useradd -r -g medai medai
COPY --from=builder /app/target/*.jar app.jar
RUN mkdir -p /app/uploads && chown -R medai:medai /app
USER medai
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
