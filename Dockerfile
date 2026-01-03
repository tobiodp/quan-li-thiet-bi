# Sử dụng Java 21 base image
FROM eclipse-temurin:21-jdk

# Tạo thư mục làm việc
WORKDIR /app

# Copy JAR file vào container
COPY target/ResourcesManagement-0.0.1-SNAPSHOT.jar app.jar

# Expose port (Render sẽ tự động map port)
EXPOSE 10000

# Chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]

