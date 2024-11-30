package org.openjfx.miniprojet;

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

public class EntryPageController {
    @FXML
    public void handleSignInButton(ActionEvent event) throws IOException {
        // Loading the login fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("loginPopUP.fxml"));
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
