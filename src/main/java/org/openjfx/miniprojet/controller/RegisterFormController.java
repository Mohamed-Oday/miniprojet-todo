package org.openjfx.miniprojet.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.openjfx.miniprojet.dao.UserDAO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

public class RegisterFormController {
    @FXML private TextField userName;
    @FXML private PasswordField password;
    @FXML private Button signButton;

    private Stage landingPageStage;
    private Stage loginFormStage;
    private final UserDAO userDAO = new UserDAO();

    public void setLandingPageStage(Stage stage) {
        this.landingPageStage = stage;
    }

    public void setSignInStage(Stage stage) {
        this.loginFormStage = stage;
    }

    @FXML
    public void handleSignAccount() throws IOException {
        returnToSignIn();
    }

    @FXML
    public void handleSignUpButton() throws IOException, SQLException {
        String username = userName.getText();
        String pass = password.getText();

        if (username.isEmpty() || pass.isEmpty()) {
            showErrorAlert("Validation Error", "Username and password cannot be empty!");
            return;
        }

        if (!isValidPassword(pass)){
            showErrorAlert("Invalid Password",
                    """
                            Password must contain:
                            -At least 8 characters
                            -At least one uppercase letter
                            -At least one number""");
            return;
        }

        if (userDAO.registerUser(username, pass)) {
            showSuccessAlert();
            returnToSignIn();
        }else{
            showErrorAlert("Registration Error", "Username already exists!");
        }
    }

    private static boolean isValidPassword(String password){
        return password.length() >= 8 && password.matches(".*\\d.*") && password.matches(".*[A-Z].*");
    }

    public void returnToSignIn() throws IOException {
        // Loading the signUp fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/openjfx/miniprojet/assets/fxml/LoginForm.fxml"));
        Parent root = loader.load();

        LoginFormController loginFormController = loader.getController();

        Stage signUpStage = (Stage) signButton.getScene().getWindow();

        // Creating new stage for the signUp page
        Stage signInStage = new Stage();
        signInStage.setTitle("Sign In");
        signInStage.setScene(new Scene(root));
        signInStage.initStyle(StageStyle.UTILITY);
        signInStage.initModality(Modality.APPLICATION_MODAL);

        loginFormController.setLandingPageStage(landingPageStage);
        loginFormController.setLoginFormStage(signInStage);

        // Setting the login stage and signUp stage
        signInStage.show();
        signUpStage.close();
    }

    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("/org/openjfx/miniprojet/assets/styles/alert.css")).toExternalForm()
        );

        Stage stage = (Stage) dialogPane.getScene().getWindow();
        stage.setResizable(false);

        alert.showAndWait();
    }

    private void showSuccessAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText("Account created successfully!");

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("/org/openjfx/miniprojet/assets/styles/alert.css")).toExternalForm()
        );
        dialogPane.getStyleClass().add("success");

        Stage stage = (Stage) dialogPane.getScene().getWindow();
        stage.setResizable(false);

        alert.showAndWait();
    }



}
