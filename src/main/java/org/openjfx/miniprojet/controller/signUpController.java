package org.openjfx.miniprojet.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Controller class for handling sign-up functionality.
 * @version 1.0
 * @since 1.0
 * @author Sellami Mohamed Oday
 * @see BCrypt
 */
public class signUpController {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/accounts";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "admin";
    private static final Logger log = LoggerFactory.getLogger(signUpController.class);

    @FXML
    private PasswordField password;

    @FXML
    private TextField userName;

    @FXML
    private Label signLabel;

    private Stage entryStage;

    /**
     * Sets the entry stage.
     *
     * @param entryStage the entry stage to set
     */
    public void setEntryStage(Stage entryStage) {
        this.entryStage = entryStage;
    }

    /**
     * Sets the sign-in stage.
     *
     * @param signInStage the sign-in stage to set
     */
    public void setSignInStage(Stage signInStage) {
        this.entryStage = signInStage;
    }

    /**
     * Handles the sign in label action by returning to the sign-in screen.
     *
     * @param event the mouse event that triggered the action
     * @throws IOException if the FXML file cannot be loaded
     */
    @FXML
    public void handleSignAccount(MouseEvent event) throws IOException {
        returnToSignIn();
    }

    /**
     * Handles the sign-up button action by validating the password, hashing it, and inserting the user into the database.
     *
     * @throws IOException if the FXML file cannot be loaded
     */
    @FXML
    public void handleSignUpButton() throws IOException {
        String username = userName.getText();
        String pass = password.getText();

        if (!isValidPassword(pass)){
            System.out.println("Password is not valid");
            return;
        }
        // hashing the password
        String hashedPassword = hashPassword(pass);

        // Inserting the user into the database
        if (insertUser(username, hashedPassword)){
            System.out.println("User added successfully");
            returnToSignIn();
        }
    }

    /**
     * Hashes the given password using BCrypt.
     *
     * @param password the password to hash
     * @return the hashed password
     */
    private static String hashPassword(String password){
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    /**
     * Inserts a new user into the database.
     *
     * @param username the username of the new user
     * @param pass the hashed password of the new user
     * @return true if the user was added successfully, false otherwise
     */
    @FXML
    private static boolean insertUser(String username, String pass){
        String insertQueue = "INSERT INTO userinforamtion (username, password) VALUES (?, ?)";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement preparedStatement = connection.prepareStatement(insertQueue)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, pass);

            int rowAffected = preparedStatement.executeUpdate();
            if (rowAffected > 0){
                System.out.println("User added successfully");
                return true;
            }else{
                System.out.println("Failed to add user");
                return false;
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Validates the given password based on length and character requirements.
     *
     * @param password the password to validate
     * @return true if the password is valid, false otherwise
     */
    private static boolean isValidPassword(String password){
        return password.length() >= 8 && password.matches(".*\\d.*") && password.matches(".*[A-Z].*");
    }

    /**
     * Returns to the sign-in screen by loading the login FXML and displaying it in a new stage.
     *
     * @throws IOException if the FXML file cannot be loaded
     */
    public void returnToSignIn() throws IOException {
        // Loading the signUp fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/openjfx/miniprojet/assets/fxml/loginPopUP.fxml"));
        Parent root = loader.load();

        loginPopUPController loginPopUPController = loader.getController();

        Stage signUpStage = (Stage) signLabel.getScene().getWindow();

        // Creating new stage for the signUp page
        Stage signInStage = new Stage();
        signInStage.setTitle("Sign In");
        signInStage.setScene(new Scene(root));
        signInStage.initStyle(StageStyle.UTILITY);
        signInStage.initModality(Modality.APPLICATION_MODAL);

        loginPopUPController.setEntryStage(entryStage);
        loginPopUPController.setLoginStage(signInStage);

        // Setting the login stage and signUp stage
        signInStage.show();
        signUpStage.close();
    }
}