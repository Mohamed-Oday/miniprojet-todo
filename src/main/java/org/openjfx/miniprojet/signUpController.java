package org.openjfx.miniprojet;

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
    private Stage signInStage;

    public void setEntryStage(Stage entryStage) {
        this.entryStage = entryStage;
    }

    public void setSignInStage(Stage signInStage) {
        this.signInStage = signInStage;
    }

    @FXML
    public void handleSignAccount(MouseEvent event) throws IOException {
        returnToSignIn();
    }

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

    private static String hashPassword(String password){
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

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

    private static boolean isValidPassword(String password){
        if (password.length() >= 8 && password.matches(".*\\d.*") && password.matches(".*[A-Z].*")){
            return true;
        }
        return false;
    }

    public void returnToSignIn() throws IOException {
        // Loading the signUp fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("loginPopUP.fxml"));
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
