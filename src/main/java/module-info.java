module com.example.kryptozad2 {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.kryptozad2 to javafx.fxml;
    exports com.example.kryptozad2;
}