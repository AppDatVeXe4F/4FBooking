# 🚌 4FBooking - Ứng dụng Đặt Vé Xe Buýt

Ứng dụng di động đặt vé xe buýt hiện đại được xây dựng bằng Kotlin và Jetpack Compose, tích hợp với Firebase để quản lý người dùng và dữ liệu.

## 📱 Giới thiệu

4FBooking là ứng dụng đặt vé xe buýt trực tuyến, cho phép người dùng tìm kiếm, đặt chỗ và quản lý vé xe một cách dễ dàng. Ứng dụng hỗ trợ đa ngôn ngữ (Tiếng Việt và Tiếng Anh) với giao diện người dùng hiện đại và thân thiện.

## ✨ Tính năng chính

### 🔐 Xác thực người dùng
- Đăng ký tài khoản mới
- Đăng nhập với email/password
- Đăng nhập ẩn danh (Guest)
- Quên mật khẩu và đặt lại mật khẩu
- Quản lý hồ sơ người dùng

### 🎫 Đặt vé
- Tìm kiếm chuyến xe theo điểm đi, điểm đến và ngày
- Lọc chuyến xe theo nhiều tiêu chí (giá, thời gian, loại ghế, khoảng cách)
- Chọn ghế trên sơ đồ xe
- Điền thông tin khách hàng
- Thanh toán và tạo mã QR

### 📋 Quản lý vé
- Xem danh sách vé đã đặt (Sắp tới, Đã hoàn thành, Đã hủy)
- Xem chi tiết vé
- Hiển thị mã QR cho nhân viên
- Hủy vé và xem thông tin hoàn tiền

### 👤 Hồ sơ và Cài đặt
- Xem và chỉnh sửa thông tin cá nhân
- Thay đổi avatar
- Chọn ngôn ngữ (Tiếng Việt/English)
- Trung tâm hỗ trợ với thông tin liên hệ
- Thông báo ứng dụng
- Đăng xuất

### 🌐 Đa ngôn ngữ
- Hỗ trợ Tiếng Việt và Tiếng Anh
- Chuyển đổi ngôn ngữ dễ dàng trong ứng dụng
- Lưu lựa chọn ngôn ngữ của người dùng

## 🛠️ Công nghệ sử dụng

### Frontend
- **Kotlin** - Ngôn ngữ lập trình chính
- **Jetpack Compose** - UI framework hiện đại
- **Material Design 3** - Design system
- **Navigation Compose** - Điều hướng trong ứng dụng
- **Coil** - Thư viện load ảnh

### Backend & Services
- **Firebase Authentication** - Xác thực người dùng
- **Cloud Firestore** - Cơ sở dữ liệu NoSQL
- **Firebase Analytics** - Phân tích hành vi người dùng

### Thư viện khác
- **ZXing** - Tạo và đọc mã QR
- **Coroutines** - Xử lý bất đồng bộ
- **Activity Result API** - Xử lý kết quả từ các activity khác

## 📋 Yêu cầu hệ thống

- **Android Studio** - Hedgehog | 2023.1.1 trở lên
- **JDK** - 11 trở lên
- **Min SDK** - 24 (Android 7.0)
- **Target SDK** - 36
- **Kotlin** - 1.9.0 trở lên
- **Gradle** - 8.0 trở lên

## 🚀 Cài đặt

### 1. Clone repository
```bash
git clone https://github.com/AppDatVeXe4F/4FBooking.git
cd 4FBooking
```

### 2. Cấu hình Firebase

1. Tạo một dự án Firebase mới tại [Firebase Console](https://console.firebase.google.com/)
2. Thêm ứng dụng Android vào dự án Firebase
3. Tải file `google-services.json` và đặt vào thư mục `app/`
4. Bật các dịch vụ sau trong Firebase Console:
   - Authentication (Email/Password và Anonymous)
   - Cloud Firestore Database

### 3. Build và chạy

1. Mở project trong Android Studio
2. Đợi Gradle sync hoàn tất
3. Kết nối thiết bị Android hoặc khởi động emulator
4. Chạy ứng dụng bằng cách nhấn `Run` hoặc `Shift + F10`

## 📁 Cấu trúc dự án

```
app/src/main/java/com/example/a4f/
├── data/                    # Data models và repositories
│   ├── Ticket.kt
│   ├── TicketListViewModel.kt
│   ├── FirestoreRepository.kt
│   └── FirestoreService.kt
├── navigation/              # Navigation configuration
│   ├── AppNavigation.kt
│   └── BottomNavItem.kt
├── screens/                 # Các màn hình của ứng dụng
│   ├── booking/            # Màn hình đặt vé
│   │   ├── FindTripScreen.kt
│   │   ├── SelectSeatScreen.kt
│   │   ├── FillInfoScreen.kt
│   │   ├── PaymentScreen.kt
│   │   └── QRCodeScreen.kt
│   ├── HomeScreen.kt
│   ├── LoginScreen.kt
│   ├── RegisterScreen.kt
│   ├── ProfileScreen.kt
│   ├── ProfileSettingsScreen.kt
│   ├── MyTicketsScreen.kt
│   ├── TicketDetailScreen.kt
│   ├── NotificationsScreen.kt
│   ├── SupportCenterScreen.kt
│   └── ...
├── ui/                      # UI theme và components
│   └── theme/
├── utils/                    # Utilities
│   └── LocaleHelper.kt
└── MainActivity.kt          # Entry point

app/src/main/res/
├── values/                   # String resources (Tiếng Việt)
│   └── strings.xml
└── values-en/               # String resources (English)
    └── strings.xml
```

## 🎨 Giao diện

Ứng dụng sử dụng Material Design 3 với màu sắc chủ đạo:
- **Primary Color**: Teal Green (#49736E)
- **Accent Color**: Purple (#9C27B0)
- **Background**: White và Light Gray

## 📱 Các màn hình chính

1. **Splash Screen** - Màn hình khởi động
2. **Onboarding** - Hướng dẫn sử dụng lần đầu
3. **Login/Register** - Đăng nhập và đăng ký
4. **Home** - Trang chủ với tìm kiếm, tin tức và ưu đãi
5. **Find Trip** - Tìm kiếm chuyến xe
6. **Select Seat** - Chọn ghế ngồi
7. **Fill Info** - Điền thông tin khách hàng
8. **Payment** - Thanh toán
9. **QR Code** - Hiển thị mã QR vé
10. **My Tickets** - Quản lý vé đã đặt
11. **Profile** - Hồ sơ và cài đặt

## 🔧 Cấu hình

### Firebase Configuration
Đảm bảo file `google-services.json` được đặt đúng vị trí và cấu hình Firestore rules:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
    match /trips/{tripId} {
      allow read: if true;
      allow write: if request.auth != null;
    }
    match /bookings/{bookingId} {
      allow read, write: if request.auth != null;
    }
  }
}
```

## 🤝 Đóng góp

Chúng tôi hoan nghênh mọi đóng góp! Vui lòng:

1. Fork repository
2. Tạo feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit các thay đổi (`git commit -m 'Add some AmazingFeature'`)
4. Push lên branch (`git push origin feature/AmazingFeature`)
5. Mở Pull Request

## 📝 License

Dự án này được phát triển bởi nhóm **AppDatVeXe4F**.

## 👥 Thành viên nhóm

- **itkhair05** - Dương Thế Khải
- **Knuwlottie** - Huỳnh Lê Khả Như
- **VanTruong475** - Nguyễn Văn Trường
- **NhatThuy26n** - Tống Nhật Thúy

## 📞 Liên hệ

Nếu có bất kỳ câu hỏi hoặc đề xuất nào, vui lòng tạo một [Issue](https://github.com/AppDatVeXe4F/4FBooking/issues) trên GitHub.

---

**Made with ❤️ by AppDatVeXe4F Team**

