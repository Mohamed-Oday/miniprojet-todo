package org .openjfx.miniprojet;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.Objects;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        String check = "SELECT * FROM saveduser";
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/accounts", "root", "admin");
             PreparedStatement preparedStatement = connection.prepareStatement(check)) {

            FXMLLoader fxmlLoader;
            ResultSet resultSet = preparedStatement.executeQuery();
            Scene scene;
            if (resultSet.next()){
                fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Main.fxml"));
                scene = new Scene(fxmlLoader.load());
                Controller controller = fxmlLoader.getController();
                if (controller != null){
                    controller.setUserName(resultSet.getString("username"));
                }else{
                    System.out.println("Controller is null");
                }
            }else{
                fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("EntryPage.fxml"));
                scene = new Scene(fxmlLoader.load());
            }
            String css = Objects.requireNonNull(this.getClass().getResource("styles/style.css")).toExternalForm();
            scene.getStylesheets().add(css);
            stage.setTitle("ToDo List");
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