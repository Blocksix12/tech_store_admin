# Sử dụng image JDK 17 chính thức
FROM eclipse-temurin:25-jdk

# Tác giả
LABEL authors="NvkhoaDev"

# Tạo thư mục ứng dụng trong container
WORKDIR /app

# Sao chép file JAR vào thư mục làm việc trong container
COPY target/tech-store-0.0.1-SNAPSHOT.jar /app/tech-store-0.0.1-SNAPSHOT.jar

# Chạy ứng dụng khi container khởi động
ENTRYPOINT ["java", "-jar", "/app/tech-store-0.0.1-SNAPSHOT.jar"]

EXPOSE 8081