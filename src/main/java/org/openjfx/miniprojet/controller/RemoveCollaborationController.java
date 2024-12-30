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

    public class RemoveCollaborationController {

        private final CollaborationDAO collaborationDAO = new CollaborationDAO();

        @FXML JFXListView<String> collaborationList;

        private String owner;
        private String category;
        private Stage removeCollaborationStage;
        private AppController appController;

        public void setOwner(String owner) {
            this.owner = owner;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public void setAppController(AppController appController) {
            this.appController = appController;
        }

        public void setRemoveCollaborationStage(Stage removeCollaborationStage) {
            this.removeCollaborationStage = removeCollaborationStage;
        }

        public void initialize() {
            Platform.runLater(this::loadCollaborators);
        }

        private void loadCollaborators() {
            collaborationList.getItems().clear();
            collaborationList.getItems().addAll(collaborationDAO.getCollaborators(owner, category));
        }

        public void handleCancel() {
            removeCollaborationStage.close();
        }

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

        private boolean confirmAction(String title, String message, String collaborator) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(title);
            alert.setHeaderText(message + " " + collaborator + "?");
            alert.setContentText("This action cannot be undone.");
            Optional<ButtonType> result = alert.showAndWait();
            return result.isPresent() && result.get() == ButtonType.OK;
        }

    }
