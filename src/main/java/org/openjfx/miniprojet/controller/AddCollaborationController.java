package org.openjfx.miniprojet.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.openjfx.miniprojet.dao.CollaborationDAO;
import org.openjfx.miniprojet.model.Permission;

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

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setCollaborationStage(Stage collaborationStage) {
        this.collaborationStage = collaborationStage;
    }

    public void initialize() {
        ToggleGroup permissionGroup = new ToggleGroup();
        read.setToggleGroup(permissionGroup);
        write.setToggleGroup(permissionGroup);
        read.setSelected(true);
    }

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

    public void handleCancelButton() {
        collaborationStage.close();
    }
}
