module org.openjfx.miniprojet {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;


    opens org.openjfx.miniprojet to javafx.fxml;
    exports org.openjfx.miniprojet;
}