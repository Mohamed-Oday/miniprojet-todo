package org.openjfx.miniprojet;

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

    public void setLoginStage(Stage loginStage) {
        this.loginStage = loginStage;
    }

    public void setEntryStage(Stage entryStage) {
        this.entryStage = entryStage;
    }

    @FXML
    public void handleCreateAccount() throws IOException {
        // Loading the signUp fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("signUp.fxml"));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
            Parent root = loader.load();

            Controller controller = loader.getController();
            controller.setUserName(userName.getText());

            // Creating the scene for our app
            Scene scene = new Scene(root);
            entryStage.setScene(scene);
            entryStage.setTitle("ToDo App");
            loginStage.close();
            entryStage.show();
        }else{
            System.out.println("Login Failed");
        }
    }

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
