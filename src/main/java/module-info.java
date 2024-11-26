module org.openjfx.miniprojet {
    requires javafx.fxml;
    requires com.google.gson;
    requires com.jfoenix;
    requires javafx.controls;
    requires java.sql;
    requires spring.security.crypto;
    requires org.slf4j;


    opens org.openjfx.miniprojet to javafx.fxml;
    exports org.openjfx.miniprojet;
}