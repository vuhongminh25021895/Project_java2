# Project_java2 - Auctioning App

## 1. Mo ta bai toan va pham vi he thong

Project xay dung he thong dau gia san pham gom 2 thanh phan chinh:

- **Server**: ung dung Spring Boot cung cap REST API cho dang ky, dang nhap, quan ly san pham dau gia, dat gia, nap tien va cap nhat realtime.
- **Client**: ung dung JavaFX cho nguoi dung tuong tac voi he thong dau gia qua giao dien desktop.

Pham vi he thong bao gom dang ky/dang nhap tai khoan, xem danh sach san pham dau gia, dang san pham, nap tien vao vi, dat gia, xem bieu do lich su gia, cap nhat gia realtime va chuc nang quan tri vien xoa san pham.

## 2. Cong nghe su dung va moi truong chay

### Cong nghe

- Java 21
- Maven
- Spring Boot 3.2.x
- JavaFX 21
- MySQL
- JDBC MySQL Connector
- OkHttp
- Gson
- Server-Sent Events (SSE) cho cap nhat realtime

### Yeu cau cai dat

Can cai dat cac thanh phan sau:

- JDK 21 tro len
- Maven 3.9 tro len
- MySQL Server hoac XAMPP co bat MySQL
- Git hoac cong cu giai nen source code

Project hien **chua co Maven Wrapper** (`mvnw`), vi vay may chay can co lenh `mvn` trong `PATH`.

Kiem tra moi truong:

```bash
java -version
mvn -version
mysql --version
```

### Cau hinh database

Server ket noi MySQL theo cau hinh trong `server/src/main/java/Database/DatabaseConnection.java`:

- Host: `localhost`
- Port: `3306`
- Database: `Bidding_database`
- User: `root`
- Password: rong

Tao database truoc khi chay server:

```sql
CREATE DATABASE IF NOT EXISTS Bidding_database
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;
```

Co the tao bang MySQL CLI tren macOS/Linux/Windows:

```bash
mysql -u root -e "CREATE DATABASE IF NOT EXISTS Bidding_database CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
```

Neu dung XAMPP, co the tao database `Bidding_database` bang phpMyAdmin. Cac bang `users`, `auction_products`, `auction_product_bids` se duoc server tu tao khi ung dung chay.

## 3. Cau truc thu muc

```text
.
|-- pom.xml                         # Maven parent project
|-- README.md
|-- server/
|   |-- pom.xml                     # Module Spring Boot server
|   `-- src/main/java/
|       |-- Application/            # Entry point ServerApplication
|       |-- Controller/             # REST API va SSE endpoint
|       |-- Dao/                    # Xu ly truy van MySQL/JDBC
|       |-- Database/               # Cau hinh ket noi database
|       `-- Dto/                    # Lop request/response
`-- client/
    |-- pom.xml                     # Module JavaFX client
    `-- src/main/
        |-- java/
        |   |-- Launcher.java       # Entry point client
        |   |-- AuctionApp.java     # JavaFX Application
        |   |-- Network/            # Cau hinh URL server va HTTP client
        |   |-- Service/            # Goi API server
        |   |-- StageManager/       # Dieu huong man hinh
        |   |-- View/               # Controller JavaFX
        |   `-- Dto/                # Lop request/response
        `-- resources/              # FXML va CSS
```

## 4. Lenh build va chay chuong trinh

Tat ca lenh duoi day chay tai thu muc goc project:

```bash
cd Real-Final
```

### Build va install toan bo project

Lenh nay dung duoc tren macOS, Linux, Windows PowerShell va Windows CMD neu `mvn` da nam trong `PATH`:

```bash
mvn clean install
```

Neu muon bo qua test:

```bash
mvn clean install -DskipTests
```

Nen chay lenh build/install it nhat mot lan truoc khi chay client, vi module `client` co dependency toi module `server`.
Neu bo qua buoc nay va chay rieng `mvn -pl client javafx:run`, Maven co the bao thieu `auction:server:jar:1.0-SNAPSHOT`.

### Chay server

Mo Terminal/PowerShell/CMD thu nhat:

```bash
mvn -pl server spring-boot:run
```

Server chay mac dinh tai:

```text
http://localhost:8080
```
Dia chi tren dung khi chay client tren cung may voi server.

Neu client chay tren may khac qua Tailscale, dung dia chi Tailscale cua may server:

```text
http://100.117.129.110:8080
```

### Chay client tren macOS/Linux

Mo Terminal thu hai:

```bash
AUCTION_SERVER_URL=http://localhost:8080 mvn -pl client javafx:run
```

Neu ket noi qua Tailscale:

```bash
AUCTION_SERVER_URL=http://100.117.129.110:8080 mvn -pl client javafx:run
```


### Chay client tren Windows PowerShell

Mo PowerShell thu hai:

```powershell
$env:AUCTION_SERVER_URL="http://localhost:8080"
mvn -pl client javafx:run
```

### Chay client tren Windows CMD

Mo CMD thu hai:

```bat
set AUCTION_SERVER_URL=http://localhost:8080
mvn -pl client javafx:run
```

Bien moi truong `AUCTION_SERVER_URL` giup client ket noi dung server can dung:

- Neu client va server chay tren cung may: `http://localhost:8080`
- Neu client ket noi qua Tailscale: `http://<TAILSCALE_IP_CUA_MAY_SERVER>:8080`

Trong source code hien tai, URL mac dinh cua client la `http://100.117.129.110:8080`. Neu Tailscale IP cua may server khac dia chi nay, can dat lai `AUCTION_SERVER_URL`.


## 5. Thu tu chay Server/Client

Thuc hien theo dung thu tu:

1. Bat MySQL Server hoac bat MySQL trong XAMPP.
2. Tao database `Bidding_database` neu chua co.
3. Mo Terminal/PowerShell/CMD tai thu muc goc project.
4. Build/install project:

   ```bash
   mvn clean install -DskipTests
   ```

5. Chay server:

   ```bash
   mvn -pl server spring-boot:run
   ```

6. Doi den khi console bao server da khoi dong tren port `8080`.
7. Mo Terminal/PowerShell/CMD thu hai.
8. Chay client theo he dieu hanh dang dung:

- Neu client va server chay tren cung may: `http://localhost:8080`
- Neu client ket noi qua Tailscale: `http://<TAILSCALE_IP_CUA_MAY_SERVER>:8080`

  macOS/Linux:

   ```bash
   AUCTION_SERVER_URL=http://localhost:8080 mvn -pl client javafx:run
   ```

   Neu ket noi qua Tailscale:

   ```bash
   AUCTION_SERVER_URL=http://100.117.129.110:8080 mvn -pl client javafx:run
   ```

  Windows PowerShell:

   ```powershell
   $env:AUCTION_SERVER_URL="http://localhost:8080"
   mvn -pl client javafx:run
   ```

  Windows CMD:

   ```bat
   set AUCTION_SERVER_URL=http://localhost:8080
   mvn -pl client javafx:run
   ```

9. Dang nhap hoac dang ky tai khoan tren client.

Tai khoan admin mac dinh duoc server tu tao/cap nhat khi khoi dong:

```text
Username: admin
Password: admin123
```

De chay nhieu client cung luc, mo them Terminal/PowerShell/CMD va chay lai lenh client tuong ung voi he dieu hanh.

## 6. Danh sach chuc nang da hoan thanh

- Dang ky tai khoan nguoi dung.
- Dang nhap tai khoan nguoi dung va admin.
- Tu tao/cap nhat tai khoan admin mac dinh `admin/admin123`.
- Phan quyen giao dien theo vai tro User/Admin.
- Hien thi danh sach san pham dau gia.
- Dang san pham dau gia voi danh muc, ten san pham, mo ta, gia khoi diem, gia mua dut va thoi gian ket thuc.
- Nguoi dung nap tien vao vi va xem so du.
- Dat gia san pham theo so du hien co.
- Kiem tra gia dat toi thieu theo quy tac lon hon gia hien tai 2%.
- Chan admin dat gia, chan nguoi ban dat gia san pham cua chinh minh.
- Hoan tien cho nguoi dang tra cao nhat khi co nguoi khac tra gia cao hon.

## 7. Link
https://drive.google.com/drive/folders/1sfORUvXd4X0TkX8wsCb5249m0fd729nR?dmr=1&ec=wgc-drive-%5Bmodule%5D-goto
- Tu ket thuc phien dau gia khi dat gia mua dut hoac het thoi gian.
- Gia han thoi gian dau gia them 5 phut neu co luot dat gia trong 15 phut cuoi.
- Cap nhat gia, trang thai va so du realtime bang Server-Sent Events.
- Hien thi bieu do line chart lich su gia dat cho tung san pham.
- Admin xoa san pham va hoan tien cho nguoi dang tra cao nhat neu co.
- Luu du lieu vao MySQL va tu tao cac bang can thiet khi server chay.
