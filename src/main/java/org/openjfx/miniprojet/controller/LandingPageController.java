package org.openjfx.miniprojet.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class LandingPageController {

    /**
     * Handles the action of the sign-in button.
     * Loads the login form FXML, sets up the login form controller, and displays the login form in a new stage.
     *
     * @param event the action event triggered by clicking the sign-in button
     * @throws IOException if an I/O error occurs during loading the FXML
     */
    @FXML
    public void handleSignInButton(ActionEvent event) throws IOException {
        // Loading the login fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/openjfx/miniprojet/assets/fxml/LoginForm.fxml"));
        Parent root = loader.load();

        // Getting login Controller
        LoginFormController loginFormController = loader.getController();

        // Getting the entry stage
        Stage landingPageStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Creating new stage for the login pop-up
        Stage loginFormStage = new Stage();
        loginFormStage.setTitle("Sign In");
        loginFormStage.setScene(new Scene(root));
        loginFormStage.initStyle(StageStyle.UTILITY);
        loginFormStage.initModality(Modality.APPLICATION_MODAL);

        // Setting the entry stage and login stage
        if (landingPageStage != null){
            loginFormController.setLoginFormStage(loginFormStage);
            loginFormController.setLandingPageStage(landingPageStage);
            // Show the login
            loginFormStage.show();
        }else{
            System.out.println("Error: Stage is null");
        }
    }

}