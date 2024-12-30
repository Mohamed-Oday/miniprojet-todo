package org.openjfx.miniprojet.controller;

import com.jfoenix.controls.JFXCheckBox;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.openjfx.miniprojet.dao.UserDAO;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Controller class for the login form.
 * This class handles the UI logic for user login, including validation and interaction with the database.
 * It interacts with the UserDAO to validate user credentials and manage user sessions.
 * It also handles navigation to the registration form and the main application page.
 *
 */
public class LoginFormController {
    @FXML private JFXCheckBox stayLoggedIn;
    @FXML private PasswordField password;
    @FXML private TextField userName;
    @FXML private Button createButton;

    private Stage landingPageStage;
    private Stage loginFormStage;
    private final UserDAO userDAO = new UserDAO();

    /**
     * Sets the login form stage.
     *
     * @param stage the stage to set
     */
    public void setLoginFormStage(Stage stage) {
        this.loginFormStage = stage;
    }

    /**
     * Sets the landing page stage.
     *
     * @param stage the stage to set
     */
    public void setLandingPageStage(Stage stage) {
        this.landingPageStage = stage;
    }

    /**
     * Handles the action of the create account button.
     * Loads the registration form FXML, sets up the registration form controller, and displays the registration form in a new stage.
     *
     * @throws IOException if an I/O error occurs during loading the FXML
     */
    @FXML
    public void handleCreateAccount() throws IOException{
        // Loading the signUp fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/openjfx/miniprojet/assets/fxml/RegisterForm.fxml"));
        Parent root = loader.load();

        // Getting the signUp Controller
        RegisterFormController registerFormController = loader.getController();

        // Getting the login stage
        Stage loginStage = (Stage) createButton.getScene().getWindow();

        // Creating new stage for the signUp page
        Stage signUpStage = new Stage();
        signUpStage.setTitle("Sign Up");
        signUpStage.setScene(new Scene(root));
        signUpStage.initStyle(StageStyle.UTILITY);
        signUpStage.initModality(Modality.APPLICATION_MODAL);

        // Setting the signUp stage and passing entryStage
        registerFormController.setLandingPageStage(landingPageStage);
        registerFormController.setSignInStage(loginStage);

        // Setting the login stage and signUp stage
        signUpStage.show();
        loginStage.close();
    }

    /**
     * Handles the action of the sign-in button.
     * Validates the user credentials and navigates to the main application page if valid.
     * Displays an error alert if the credentials are invalid.
     *
     * @throws IOException if an I/O error occurs during loading the FXML
     * @throws SQLException if a database access error occurs
     */
    @FXML
    public void handleSignInButton() throws IOException, SQLException {
        String username = userName.getText();
        String pass = password.getText();

        if (username.isEmpty() || pass.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Error");
            alert.setHeaderText(null);
            alert.setContentText("Please enter both username and password!");
            alert.showAndWait();
            return;
        }

        if (userDAO.isValidLogin(username, pass)) {
            if (stayLoggedIn.isSelected()){
                userDAO.saveUser(username);
            }
            // Loading Main fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/openjfx/miniprojet/assets/fxml/AppPage.fxml"));
            Parent root = loader.load();

            AppController controller = loader.getController();
            controller.setUser(userName.getText());

            // Creating the scene for our app
            Scene scene = new Scene(root);
            Stage appStage = new Stage();
            appStage.setScene(scene);
            appStage.setTitle("ToDo App");
            loginFormStage.close();
            landingPageStage.close();
            appStage.setMaximized(true);
            appStage.show();
        }else{
            System.out.println("Invalid Login");
        }

    }
}