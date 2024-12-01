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

/**
 * Controller class for the entry page.
 * Handles the sign-in button action to display the login pop-up.
 *
 * @author Sellami Mohamed Oday
 * @version 1.0
 * @since 1.0
 * @see FXMLLoader#load()
 * @see Stage#show()
 */
public class EntryPageController {

    /**
     * Handles the sign-in button action event.
     * Loads the login FXML, sets up the login controller, and displays the login pop-up stage.
     *
     * @param event the action event triggered by the sign-in button
     * @throws IOException if the FXML file cannot be loaded
     * @see FXMLLoader#load()
     * @see Stage#show()
     */
    @FXML
    public void handleSignInButton(ActionEvent event) throws IOException {
        // Loading the login fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/openjfx/miniprojet/assets/fxml/loginPopUP.fxml"));
        Parent root = loader.load();

        // Getting login Controller
        loginPopUPController loginController = loader.getController();

        // Getting the entry stage
        Stage entryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Creating new stage for the login pop-up
        Stage loginStage = new Stage();
        loginStage.setTitle("Sign In");
        loginStage.setScene(new Scene(root));
        loginStage.initStyle(StageStyle.UTILITY);
        loginStage.initModality(Modality.APPLICATION_MODAL);

        // Setting the entry stage and login stage
        if (entryStage != null){
            loginController.setLoginStage(loginStage);
            loginController.setEntryStage(entryStage);
            // Show the login
            loginStage.show();
        }else{
            System.out.println("Error: Stage is null");
        }
    }
}