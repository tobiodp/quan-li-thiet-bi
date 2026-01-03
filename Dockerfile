# Stage 1: Build Maven project
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copy Maven wrapper và pom.xml
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x ./mvnw

# Copy source code
COPY src ./src

# Build project
RUN ./mvnw clean package -DskipTests

# Stage 2: Run application
FROM eclipse-temurin:21-jdk
WORKDIR /app

# Copy JAR từ build stage
COPY --from=build /app/target/ResourcesManagement-0.0.1-SNAPSHOT.jar app.jar

# Expose port (Render sẽ tự động map port)
EXPOSE 10000

# Chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]

