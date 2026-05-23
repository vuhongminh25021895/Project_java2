module com.example.naptien {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires java.sql;


    opens com.example.naptien to javafx.fxml;
    exports com.example.naptien;
}