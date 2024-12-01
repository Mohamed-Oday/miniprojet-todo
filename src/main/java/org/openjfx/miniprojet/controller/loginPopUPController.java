package org.openjfx.miniprojet.controller;

import com.jfoenix.controls.JFXCheckBox;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.io.IOException;
import java.sql.*;

/**
 * Controller class for handling login pop-up functionality.
 * @version 1.0
 * @since 1.0
 * @author Sellami Mohamed Oday
 * @see BCrypt
 */
public class loginPopUPController {

    @FXML
    private JFXCheckBox stayLoggedIn;

    @FXML
    private PasswordField password;

    @FXML
    private TextField userName;

    @FXML
    private Label createLabel;

    private Stage entryStage;
    private Stage loginStage;

    /**
     * Sets the login stage.
     *
     * @param loginStage the login stage to set
     */
    public void setLoginStage(Stage loginStage) {
        this.loginStage = loginStage;
    }

    /**
     * Sets the entry stage.
     *
     * @param entryStage the entry stage to set
     */
    public void setEntryStage(Stage entryStage) {
        this.entryStage = entryStage;
    }

    /**
     * Handles the creation of a new account by loading the sign-up FXML and displaying it in a new stage.
     *
     * @throws IOException if the FXML file cannot be loaded
     */
    @FXML
    public void handleCreateAccount() throws IOException {
        // Loading the signUp fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/openjfx/miniprojet/assets/fxml/signUp.fxml"));
        Parent root = loader.load();

        // Getting the signUp Controller
        signUpController signUpController = loader.getController();

        // Getting the login stage
        Stage loginStage = (Stage) createLabel.getScene().getWindow();

        // Creating new stage for the signUp page
        Stage signUpStage = new Stage();
        signUpStage.setTitle("Sign Up");
        signUpStage.setScene(new Scene(root));
        signUpStage.initStyle(StageStyle.UTILITY);
        signUpStage.initModality(Modality.APPLICATION_MODAL);

        // Setting the signUp stage and passing entryStage
        signUpController.setEntryStage(entryStage);
        signUpController.setSignInStage(loginStage);

        // Setting the login stage and signUp stage
        signUpStage.show();
        loginStage.close();
    }

    /**
     * Handles the sign-in button action by validating the login credentials and loading the main application scene if successful.
     *
     * @throws IOException if the FXML file cannot be loaded
     */
    @FXML
    public void handleSignInButton() throws IOException {
        String username = userName.getText();
        String pass = password.getText();

        if (isValidLogin(username, pass)){
            System.out.println("Login Successful");

            if (stayLoggedIn.isSelected()){
                String insertQuery = "INSERT INTO saveduser (username) VALUES (?)";
                try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/accounts", "root", "admin");
                     PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)){

                    preparedStatement.setString(1, username);
                    int rows = preparedStatement.executeUpdate();

                    if (rows > 0){
                        System.out.println("User saved successfully.");
                    }else{
                        System.out.println("User not saved.");
                    }

                } catch (SQLException e){
                    e.printStackTrace();
                }
            }

            // Loading Main fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/openjfx/miniprojet/assets/fxml/Main.fxml"));
            Parent root = loader.load();

            Controller controller = loader.getController();
            controller.setUserName(userName.getText());

            // Creating the scene for our app
            Scene scene = new Scene(root);
            entryStage.setScene(scene);
            entryStage.setTitle("ToDo App");
            loginStage.close();
            entryStage.setMaximized(true);
            entryStage.show();
        }else{
            System.out.println("Login Failed");
        }
    }

    /**
     * Validates the login credentials by checking the username and password against the database.
     *
     * @param username the username to validate
     * @param password the password to validate
     * @return true if the login is valid, false otherwise
     * @apiNote This method uses the BCrypt library to compare the hashed password from the database with the password entered by the user.
     */
    public boolean isValidLogin(String username, String password){
        String selectQuery = "SELECT password FROM userinforamtion WHERE username = ?";
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/accounts", "root", "admin");
             PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)){

            preparedStatement.setString(1, username);

            try (ResultSet resultSet = preparedStatement.executeQuery()){
                if (resultSet.next()){
                    String hashedPassword = resultSet.getString("password");
                    return BCrypt.checkpw(password, hashedPassword);
                }

            } catch (SQLException e){
                e.printStackTrace();
            }

        } catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }
}