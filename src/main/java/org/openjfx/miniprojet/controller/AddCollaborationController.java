package org.openjfx.miniprojet.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.openjfx.miniprojet.dao.CollaborationDAO;
import org.openjfx.miniprojet.model.Permission;

/**
 * Controller class for adding a new collaboration.
 *
 * @author Sellami Mohamed Odai
 */
public class AddCollaborationController {

    private final CollaborationDAO collaborationDAO = new CollaborationDAO();

    @FXML private TextField userField;
    @FXML private ToggleButton read;
    @FXML private ToggleButton write;
    @FXML private Label title;
    @FXML private Label permissionLabel;
    @FXML private HBox permissionHbox;
    @FXML private Label subTitle;

    private String userID;
    private String category;
    private Stage collaborationStage;

    /**
     * Sets the user ID.
     *
     * @param userID the user ID to set
     */
    public void setUserID(String userID) {
        this.userID = userID;
    }

    /**
     * Sets the category.
     *
     * @param category the category to set
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Sets the stage for adding a collaboration.
     *
     * @param collaborationStage the stage to set
     */
    public void setCollaborationStage(Stage collaborationStage) {
        this.collaborationStage = collaborationStage;
    }

    /**
     * Initializes the controller.
     * Sets up the toggle group for permission selection and selects the read permission by default.
     */
    public void initialize() {
        ToggleGroup permissionGroup = new ToggleGroup();
        read.setToggleGroup(permissionGroup);
        write.setToggleGroup(permissionGroup);
        read.setSelected(true);
    }

    /**
     * Handles the action of adding a collaboration.
     * Retrieves the user and permission from the input fields and adds the collaboration to the database.
     */
    public void addCollaboration() {
        String user = userField.getText();
        AppController controller = new AppController();
        Permission permission = read.isSelected() ? Permission.Read : Permission.Write;
        System.out.println(category);
        System.out.println(userID);
        System.out.println(user);

        if (user.isEmpty()) {
            System.out.println("Please fill in all fields");
            return;
        }
        if (controller.sendInviteAlert(user)){
            collaborationDAO.addCollaboration(category, userID, user, permission);
            //controller.showNotification("Collaboration added successfully.", "Collaboration", "has been added", category);
            collaborationStage.close();
        }
    }

    /**
     * Handles the action of canceling the add collaboration operation.
     * Closes the stage.
     */
    public void handleCancelButton() {
        collaborationStage.close();
    }
}