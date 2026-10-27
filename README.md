# Quản lý phòng ban

## 1. Giới thiệu
Dự án xây dựng ứng dụng quản lý phòng ban với các chức năng thêm, xem danh sách, tìm kiếm, sửa và xóa phòng ban.

## 2. Công nghệ sử dụng
- Backend: Java, Spring Boot, Spring Data JPA
- Frontend: Vue.js, Vite, Axios
- Cơ sở dữ liệu: PostgreSQL
- Công cụ phát triển: IntelliJ IDEA, Visual Studio Code

## 3. Cấu trúc dự án
- `backend/`: mã nguồn Spring Boot và REST API.
- `frontend/`: giao diện Vue.js.
- `database/phong_ban.sql`: câu lệnh tạo bảng PostgreSQL.

## 4. Cài đặt cơ sở dữ liệu
1. Cài đặt và khởi động PostgreSQL.
2. Tạo database có tên `quanly_phongban`.
3. Chạy file `database/phong_ban.sql` trong database vừa tạo.

## 5. Chạy Backend
Mở dự án `backend/` bằng IntelliJ IDEA.

Cấu hình biến môi trường trong Run Configuration:
- `DB_URL`: `jdbc:postgresql://localhost:5432/quanly_phongban`
- `DB_USERNAME`: `postgres`
- `DB_PASSWORD`: mật khẩu PostgreSQL của máy đang chạy ứng dụng.

Chạy lớp chính `QuanlyPhongbanApplication`.

Backend mặc định chạy tại `http://localhost:8080`.

## 6. Chạy Frontend
Mở PowerShell tại thư mục `frontend/`, sau đó chạy:

```powershell
npm.cmd install
npm.cmd run dev
```

Mở địa chỉ frontend được Vite hiển thị trên terminal, mặc định là `http://localhost:5173`.

## 7. API
| Phương thức | Đường dẫn | Chức năng |
|---|---|---|
| GET | `/api/phong-ban` | Lấy danh sách phòng ban |
| GET | `/api/phong-ban/{id}` | Lấy thông tin một phòng ban |
| POST | `/api/phong-ban` | Thêm phòng ban |
| PUT | `/api/phong-ban/{id}` | Cập nhật phòng ban |
| DELETE | `/api/phong-ban/{id}` | Xóa phòng ban |

## 8. Lưu ý
- Cần chạy PostgreSQL và Backend trước khi sử dụng đầy đủ chức năng Frontend.
- Không đưa mật khẩu cơ sở dữ liệu hoặc thông tin bí mật lên GitHub.
- Thư mục `node_modules/` và `target/` không cần đưa lên GitHub.
