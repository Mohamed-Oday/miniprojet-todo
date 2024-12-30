package org.openjfx.miniprojet.controller;

import com.jfoenix.controls.JFXListView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import org.openjfx.miniprojet.dao.CollaborationDAO;
import org.openjfx.miniprojet.model.Status;

import java.sql.SQLException;
import java.util.Optional;

/**
 * Controller class for removing a collaboration.
 * This class handles the UI logic for removing a collaborator from a task category.
 * It interacts with the CollaborationDAO to manage collaborations and updates the main application controller.
 */
public class RemoveCollaborationController {

    private final CollaborationDAO collaborationDAO = new CollaborationDAO();

    @FXML JFXListView<String> collaborationList;

    private String owner;
    private String category;
    private Stage removeCollaborationStage;
    private AppController appController;

    /**
     * Sets the owner of the task category.
     *
     * @param owner the owner to set
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * Sets the category of the task.
     *
     * @param category the category to set
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Sets the main application controller.
     *
     * @param appController the main application controller to set
     */
    public void setAppController(AppController appController) {
        this.appController = appController;
    }

    /**
     * Sets the stage for removing a collaboration.
     *
     * @param removeCollaborationStage the stage to set
     */
    public void setRemoveCollaborationStage(Stage removeCollaborationStage) {
        this.removeCollaborationStage = removeCollaborationStage;
    }

    /**
     * Initializes the controller.
     * Loads the collaborators for the specified task category.
     */
    public void initialize() {
        Platform.runLater(this::loadCollaborators);
    }

    /**
     * Loads the collaborators for the specified task category from the database.
     * Clears the current list and adds the collaborators to the list view.
     */
    private void loadCollaborators() {
        collaborationList.getItems().clear();
        collaborationList.getItems().addAll(collaborationDAO.getCollaborators(owner, category));
    }

    /**
     * Handles the action of canceling the remove collaboration operation.
     * Closes the stage.
     */
    public void handleCancel() {
        removeCollaborationStage.close();
    }

    /**
     * Handles the action of removing a collaborator.
     * Prompts the user for confirmation and removes the selected collaborator if confirmed.
     * Updates the collaborator list and notifies the main application controller.
     */
    public void handleRemoveCollaboration() {
        String selectedCollaborator = collaborationList.getSelectionModel().getSelectedItem();
        if (selectedCollaborator == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Collaborator Selected");
            return;
        }
        if (confirmAction("Remove Collaboration", "Are you sure you want to remove this collaborator", selectedCollaborator)) {
            collaborationDAO.removeCollaboration(category, owner, selectedCollaborator);
            loadCollaborators();
            handleCancel();
            appController.setLatestTaskName(selectedCollaborator);
            appController.showNotification("Collaboration Removed", "You have removed", "from the collaboration", selectedCollaborator);
        }
    }

    /**
     * Prompts the user for confirmation before performing an action.
     *
     * @param title the title of the confirmation dialog
     * @param message the message of the confirmation dialog
     * @param collaborator the name of the collaborator to be removed
     * @return true if the user confirms the action, false otherwise
     */
    private boolean confirmAction(String title, String message, String collaborator) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(message + " " + collaborator + "?");
        alert.setContentText("This action cannot be undone.");
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

}