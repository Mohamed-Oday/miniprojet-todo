package org.openjfx.miniprojet.model;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.openjfx.miniprojet.controller.Controller;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Objects;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        String check = "SELECT * FROM saveduser";
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/accounts", "root", "admin");
             PreparedStatement preparedStatement = connection.prepareStatement(check)) {

            FXMLLoader fxmlLoader;
            ResultSet resultSet = preparedStatement.executeQuery();
            Scene scene;
            if (resultSet.next()){
                fxmlLoader = new FXMLLoader(App.class.getResource("/org/openjfx/miniprojet/assets/fxml/Main.fxml"));
                scene = new Scene(fxmlLoader.load());
                Controller controller = fxmlLoader.getController();
                if (controller != null){
                    controller.setUserName(resultSet.getString("username"));
                }else{
                    System.out.println("Controller is null");
                }
                stage.setMaximized(true);
            }else{
                fxmlLoader = new FXMLLoader(App.class.getResource("/org/openjfx/miniprojet/assets/fxml/EntryPage.fxml"));
                scene = new Scene(fxmlLoader.load());
            }
                String css = Objects.requireNonNull(this.getClass().getResource("/org/openjfx/miniprojet/assets/styles/style.css")).toExternalForm();
                scene.getStylesheets().add(css);
            stage.setTitle("ToDo List");
            URL icon = getClass().getResource("/org/openjfx/miniprojet/assets/images/logo.png");
            Image appIcon = new Image(Objects.requireNonNull(icon).toString());
            stage.getIcons().add(appIcon);
            stage.setScene(scene);
            stage.show();
        } catch (SQLException e){
            e.printStackTrace();
        }

    }


    public static void main(String[] args) {
        launch();
    }
}