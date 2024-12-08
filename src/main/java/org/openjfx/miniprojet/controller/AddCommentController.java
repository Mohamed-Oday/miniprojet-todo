package org.openjfx.miniprojet.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.openjfx.miniprojet.dao.TaskDAO;
import org.openjfx.miniprojet.model.TaskImpl;

public class AddCommentController {
    @FXML private TextField commentField;

    private final TaskDAO taskDAO = new TaskDAO();
    private String userID;
    private TaskImpl task;
    private Stage addCommentStage;
    private Controller mainController;

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setTask(TaskImpl task) {
        this.task = task;
    }

    public void setAddCommentStage(Stage addCommentStage) {
        this.addCommentStage = addCommentStage;
    }

    public void setMainController(Controller mainController) {
        this.mainController = mainController;
    }

    public void handleAddButton() {
        String comment = commentField.getText();
        if (comment != null && !comment.isEmpty()) {
            taskDAO.addComment(comment, task, userID);
            mainController.setLatestTaskName(task.getName());
            mainController.setComment(comment);
            mainController.showNotification("Comment added successfully.", "Comment", "has been added", comment);;
            closeStage();
        }
    }

    public void closeStage() {
        addCommentStage.close();
    }
}
