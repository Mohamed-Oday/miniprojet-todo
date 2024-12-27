package org.openjfx.miniprojet.controller;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXRadioButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.openjfx.miniprojet.dao.CategoryDAO;
import org.openjfx.miniprojet.dao.TaskDAO;
import org.openjfx.miniprojet.model.Status;
import java.time.LocalDate;
import java.util.Objects;

import javafx.scene.control.DatePicker;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class addTaskController {

    @FXML private JFXCheckBox importantCheck;
    @FXML private TextField descriptionField;
    @FXML private DatePicker dueDateField;
    @FXML private TextField nameField;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private ToggleGroup priorityGroup;
    @FXML private JFXRadioButton high;
    @FXML private JFXRadioButton medium;
    @FXML private JFXRadioButton low;
    @FXML DatePicker startDateField;

    private final ObservableList<String> categories = FXCollections.observableArrayList();

    private final TaskDAO taskDAO = new TaskDAO();

    private final CategoryDAO categoryDAO = new CategoryDAO();

    public void initialize() {
        priorityGroup = new ToggleGroup();
        high.setToggleGroup(priorityGroup);
        medium.setToggleGroup(priorityGroup);
        low.setToggleGroup(priorityGroup);
        low.setSelected(true);
        categoryComboBox.setValue("General");
        setupDatePicker();

    }

    private String userID;
    private Stage addTaskStage;
    private Controller mainController;

    public void setAddTaskStage(Stage addTaskStage) {
        this.addTaskStage = addTaskStage;
    }

    public void setMainController(Controller mainController) {
        this.mainController = mainController;
    }

    public void setUserID(String userID) {
        this.userID = userID;
        loadCategories();
    }

    private void loadCategories() {
        categories.clear();
        categories.addAll(categoryDAO.loadCategories(userID));
        categoryComboBox.setItems(categories);
    }

    @FXML
    public void handleAddButton() {
        String name = getTaskName();
        String description = getTaskDescription();
        LocalDate startDate = startDateField.getValue();
        LocalDate dueDate = getTaskDueDate();
        String selectedCategory = categoryComboBox.getValue();
        String priority = ((JFXRadioButton) priorityGroup.getSelectedToggle()).getText();

        StringBuilder errorMessage = new StringBuilder("Please make sure that the following fields are filled correctly:\n");

        if (name == null || name.isEmpty()) {
            errorMessage.append("- Task Name\n");
        }
        if (startDate == null) {
            errorMessage.append("- Start Date\n");
        }
        if (dueDate == null) {
            errorMessage.append("- Due Date\n");
        } else if (startDate != null && !startDate.isBefore(dueDate)) {
            errorMessage.append("- Start Date should be before Due Date\n");
        }

        if (errorMessage.length() > "Please make sure that the following fields are filled correctly:\n".length()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Input");
            
            // Create custom header with icon
            ImageView icon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/org/openjfx/miniprojet/assets/images/error.png"))));
            icon.setFitWidth(32);
            icon.setFitHeight(32);
            Label headerLabel = new Label("Please Check Required Fields");
            headerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #f44336;");

            HBox header = new HBox(10);
            header.setAlignment(Pos.CENTER_LEFT);
            header.getChildren().addAll(icon, headerLabel);
            alert.getDialogPane().setHeader(new Region()); // Clear default header
            
            // Style the content
            VBox content = new VBox(10);
            Label messageLabel = new Label("The following fields are required:");
            messageLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #757575;");
            
            VBox errorList = new VBox(5);
            for (String error : errorMessage.toString().split("\n")) {
                if (error.startsWith("-")) {
                    Label errorItem = new Label(error);
                    errorItem.setStyle("-fx-text-fill: #d32f2f; -fx-font-size: 13px;");
                    errorList.getChildren().add(errorItem);
                }
            }
            
            content.getChildren().addAll(messageLabel, errorList);
            alert.getDialogPane().setContent(content);
            
            // Style the dialog pane
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.setStyle(
                "-fx-background-color: white;" +
                "-fx-padding: 20;" +
                "-fx-background-radius: 8;"
            );
            
            // Style the buttons
            dialogPane.getButtonTypes().setAll(ButtonType.OK);
            Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
            okButton.setStyle(
                "-fx-background-color: #f44336;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 8 20;" +
                "-fx-background-radius: 4;"
            );
            
            alert.showAndWait();
        } else {
            insertTask(name, description, dueDate, startDate, selectedCategory, priority);
            mainController.setLatestTaskName(name);
            addTaskStage.close();
        }
    }

    private void insertTask(String name, String description, LocalDate dueDate, LocalDate startDate, String category, String priority) {
        taskDAO.createTasks(name, description, dueDate, startDate, startDate.equals(LocalDate.now()) ? Status.Started : Status.Pending, category, priority, userID, importantCheck.isSelected());
    }

    @FXML
    public void handleCancelButton() {
        addTaskStage.close();
    }

    @FXML
    public String getTaskName() {
        return nameField.getText();
    }

    @FXML
    public String getTaskDescription() {
        return descriptionField.getText();
    }

    @FXML
    public LocalDate getTaskDueDate() {
        return dueDateField.getValue();
    }

    public void setupDatePicker() {
        dueDateField.setDayCellFactory(datePicker -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (item.isBefore(startDateField.getValue())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;");
                }
            }
        });
        startDateField.setDayCellFactory(datePicker -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (item.isBefore(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;");
                }
            }
        });
        startDateField.setValue(LocalDate.now());
    }
}