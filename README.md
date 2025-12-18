```markdown
# Tech Store Admin

Java Spring Boot application for admin panel of Tech Store.

## Tổng quan
Đây là repository backend (Spring Boot) kèm một số tài nguyên HTML cho giao diện quản trị. Mô tả ngắn: `java spring boot`.

Ngôn ngữ chính:
- HTML (~64.6%)
- Java (~35.4%)

## Tính năng chính
- Quản lý sản phẩm
- Quản lý danh mục
- Quản lý người dùng / phân quyền (Admin)
- API REST để dùng bởi giao diện quản trị

> Lưu ý: Tệp README này là mẫu — cập nhật phần "Cấu hình" và "Biến môi trường" theo thực tế của dự án.

## Branches (các nhánh hiện có)
Mình đã kiểm tra các nhánh trong repository và liệt kê bên dưới. Mô tả là suy đoán dựa trên tên nhánh — vui lòng chỉnh lại nếu cần.

- main
  - Commit: b4fd28075917beed703e6999d5139a2b217294a4
  - Mô tả: Nhánh chính (production/stable). Mã nguồn chuẩn để deploy.

- NvkhoaDev
  - Commit: 06af4d1dc735061e88d5fd2578fc97c3e8798649
  - Mô tả: Nhánh phát triển cá nhân của Nvkhoa — có thể chứa tính năng đang phát triển hoặc thay đổi không production-ready.

- QLDonHang
  - Commit: 57fb8181506a50273d8d607be88df54c4b22e80c
  - Mô tả: Có vẻ tập trung vào quản lý đơn hàng (QL = quản lý, DonHang = đơn hàng). Dùng để phát triển/khắc phục lỗi liên quan đơn hàng.

- User_Tonkho_NQL
  - Commit: 0f5e59c577a7542475da03b751671be81a01573c
  - Mô tả: Có thể chứa tính năng quản lý người dùng và tồn kho (TonKho) dành cho người quản lý (NQL).

- comment_NLP
  - Commit: fb4554f81e231961fe80a869b45898b7a3787dcb
  - Mô tả: Tính năng xử lý comment với NLP (phân tích cảm xúc, lọc spam, tự động gợi ý, v.v.)

Ghi chú: Nếu muốn, mình có thể thêm liên kết trực tiếp tới từng PR hoặc so sánh branch với main.

## Yêu cầu (Prerequisites)
- Java 17+ (hoặc phiên bản tương thích với dự án)
- Maven hoặc Gradle (tùy theo cấu hình repository)
- Database (MySQL, PostgreSQL hoặc H2 cho development)
- (Tùy chọn) Docker nếu chạy bằng container

## Cài đặt & Chạy nhanh (Quick start)

1. Clone repo:
   ```bash
   git clone https://github.com/Blocksix12/tech_store_admin.git
   cd tech_store_admin
   ```

2. Cấu hình biến môi trường hoặc file `application.properties` / `application.yml`:
   - Ví dụ với MySQL:
     ```
     spring.datasource.url=jdbc:mysql://localhost:3306/tech_store
     spring.datasource.username=root
     spring.datasource.password=your_password
     spring.jpa.hibernate.ddl-auto=update
     ```

3. Chạy ứng dụng:
   - Với Maven:
     ```bash
     ./mvnw spring-boot:run
     # hoặc
     mvn spring-boot:run
     ```
   - Với Gradle:
     ```bash
     ./gradlew bootRun
     # hoặc
     gradle bootRun
     ```

4. Mở trình duyệt:
   - Backend API thường ở: `http://localhost:8080`
   - Giao diện HTML tĩnh (nếu có) thường nằm trong `src/main/resources/static` hoặc `templates`

## Biến môi trường / Cấu hình thường dùng
- SPRING_DATASOURCE_URL
- SPRING_DATASOURCE_USERNAME
- SPRING_DATASOURCE_PASSWORD
- SPRING_PROFILES_ACTIVE
- JWT_SECRET (nếu dùng JWT)
- OTHERS_* (tùy theo dự án)

Cập nhật README sau khi xác định chính xác các biến môi trường thực tế dự án.

## Docker (tùy chọn)
Tạo `Dockerfile` nếu chưa có, ví dụ cơ bản:
```dockerfile
FROM eclipse-temurin:17-jdk-jammy
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

Build & run:
```bash
mvn clean package -DskipTests
docker build -t tech_store_admin:latest .
docker run -e SPRING_DATASOURCE_URL=... -p 8080:8080 tech_store_admin:latest
```

## Cấu trúc repository (gợi ý)
- src/main/java  — mã nguồn Java (Spring Boot)
- src/main/resources — cấu hình, templates, static
- src/test — tests
- public/static hoặc templates — tệp HTML giao diện quản trị

(Cập nhật chi tiết sau khi xem cấu trúc thực tế của repo)

## Kiểm thử
- Chạy unit / integration tests:
  ```bash
  mvn test
  # hoặc
  ./gradlew test
  ```

## Contributing
1. Fork repository
2. Tạo nhánh feature: `git checkout -b feature/ten-tinh-nang`
3. Commit & push
4. Mở Pull Request mô tả thay đổi

Khi tạo PR, chọn phương pháp merge phù hợp: merge commit (giữ lịch sử), squash (gộp commit), hoặc rebase (lịch sử sạch).

## License
Thêm thông tin license phù hợp (ví dụ MIT, Apache-2.0). Hiện chưa có file LICENSE trong README này — hãy thêm nếu cần.

## Contact
- Repository: https://github.com/Blocksix12/tech_store_admin
- Chủ repo / Maintainer: Blocksix12

---
Ghi chú: Phần "Branches" được thêm theo yêu cầu. Mình đã liệt kê tên và commit SHA cho mỗi nhánh hiện có. Nếu bạn muốn mình cập nhật mô tả chính xác cho từng nhánh, hoặc push README này lên repository (hoặc tạo PR từ nhánh mới), cho mình biết:
- bạn muốn mình push trực tiếp hay tạo Pull Request,
- tên nhánh để push (ví dụ update-readme-branches),
- commit message muốn dùng.
```
