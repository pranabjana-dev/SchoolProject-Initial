# ── Stage 1: Build ─────────────────────────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build

WORKDIR /app

# Copy pom.xml first and download dependencies (cached layer)
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Copy source and build
COPY src ./src
RUN mvn clean package -DskipTests -q

# ── Stage 2: Runtime ────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Create the data directory inside the container
RUN mkdir -p /app/school-data
ENV DATA_DIR=/app/school-data/

# Copy the built JAR
COPY --from=build /app/target/school-management-1.0.0.jar app.jar

# Health check (used by Render)
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health || exit 1

EXPOSE 8080

ENTRYPOINT ["java", "-Xmx256m", "-jar", "app.jar"]
