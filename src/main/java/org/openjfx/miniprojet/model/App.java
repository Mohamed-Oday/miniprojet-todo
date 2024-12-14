package org.openjfx.miniprojet.model;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.openjfx.miniprojet.controller.Controller;
import org.openjfx.miniprojet.dao.UserDAO;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

/**
 * The main application class for the ToDo List application.
 * It extends the JavaFX Application class and serves as the entry point for the application.
 */
public class App extends Application {

    /**
     * The main entry point for all JavaFX applications.
     * This method is called after the init method has returned, and after the system is ready for the application to begin running.
     *
     * @param stage the primary stage for this application, onto which the application scene can be set.
     */
    @Override
    public void start(Stage stage) {
        UserDAO userDAO = new UserDAO();
        try {
            ResultSet resultSet = userDAO.getSavedUser();

            // If no saved user is found, load the EntryPage.fxml scene
            if (!resultSet.next()) {
                loadAndShowScene(stage, "/org/openjfx/miniprojet/assets/fxml/EntryPage.fxml");
                return;
            }

            // Load the main scene from Main.fxml
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/org/openjfx/miniprojet/assets/fxml/Main.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Controller controller = fxmlLoader.getController();

            // Check if the controller is null
            if (controller == null) {
                System.out.println("Controller is null");
                return;
            }

            // Set the username in the controller
            controller.setUserName(resultSet.getString("username"));

            // Load and apply the stylesheet
            String css = Objects.requireNonNull(this.getClass().getResource("/org/openjfx/miniprojet/assets/styles/style.css")).toExternalForm();
            scene.getStylesheets().add(css);

            // Set the application title and icon
            stage.setTitle("ToDo List");
            URL icon = getClass().getResource("/org/openjfx/miniprojet/assets/images/logo.png");
            Image appIcon = new Image(Objects.requireNonNull(icon).toString());
            stage.getIcons().add(appIcon);

            // Set the scene and maximize the stage
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads and shows a scene from the specified FXML file.
     *
     * @param stage the stage on which the scene will be set.
     * @param fxmlPath the path to the FXML file to be loaded.
     */
    private void loadAndShowScene(Stage stage, String fxmlPath) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxmlPath));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * The main method which serves as the entry point for the application.
     *
     * @param args the command line arguments.
     */
    public static void main(String[] args) {
        launch();
    }
}