package org.openjfx.miniprojet.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.openjfx.miniprojet.dao.TaskDAO;
import org.openjfx.miniprojet.model.TaskImpl;

/**
 * Controller class for adding a new comment.
 *
 * This class handles the UI logic for adding a comment to a task.
 * It interacts with the TaskDAO to persist the comment and updates the main application controller.
 *
 * @author
 * Sellami Mohamed Odai
 */
public class AddCommentController {

    private final TaskDAO taskDAO = new TaskDAO();

    @FXML private TextField commentField;

    private String userID;
    private TaskImpl task;
    private Stage addCommentStage;
    private AppController mainController;

    /**
     * Sets the user ID.
     *
     * @param userID the user ID to set
     */
    public void setUserID(String userID) {
        this.userID = userID;
    }

    /**
     * Sets the task.
     *
     * @param task the task to set
     */
    public void setTask(TaskImpl task) {
        this.task = task;
    }

    /**
     * Sets the stage for adding a comment.
     *
     * @param addCommentStage the stage to set
     */
    public void setAddCommentStage(Stage addCommentStage) {
        this.addCommentStage = addCommentStage;
    }

    /**
     * Sets the main application controller.
     *
     * @param mainController the main application controller to set
     */
    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    /**
     * Handles the action of adding a comment.
     * Retrieves the comment from the text field and adds it to the database.
     */
    public void handleAddButton() {
        String comment = commentField.getText();
        if (comment != null && !comment.isEmpty()) {
            taskDAO.addComment(comment, task, userID);
            mainController.setLatestTaskName(task.getName());
            mainController.setComment(comment);
            mainController.showNotification("Comment added successfully.", "Comment", "has been added", comment);
            closeStage();
        }
    }

    /**
     * Handles the action of canceling the add comment operation.
     * Closes the stage.
     */
    public void handleCancelButton() {
        closeStage();
    }

    /**
     * Closes the stage.
     */
    public void closeStage() {
        addCommentStage.close();
    }

}