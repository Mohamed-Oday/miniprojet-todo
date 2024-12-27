module org.openjfx.miniprojet {
    requires javafx.fxml;
    requires com.google.gson;
    requires com.jfoenix;
    requires javafx.controls;
    requires java.sql;
    requires spring.security.crypto;
    requires org.slf4j;
    requires java.desktop;
    requires javafx.graphics;
    requires spring.security.web;
    requires javafx.media;
    requires spring.context;


    opens org.openjfx.miniprojet.assets.fxml to javafx.fxml;
    exports org.openjfx.miniprojet.controller;
    opens org.openjfx.miniprojet.controller to javafx.fxml;
    exports org.openjfx.miniprojet.model;
}