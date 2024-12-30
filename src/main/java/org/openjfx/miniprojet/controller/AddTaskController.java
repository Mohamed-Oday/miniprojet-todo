package org.openjfx.miniprojet.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.openjfx.miniprojet.dao.CategoryDAO;
import org.openjfx.miniprojet.dao.TaskDAO;
import org.openjfx.miniprojet.model.Status;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Controller class for adding a new task.
 * This class handles the UI logic for adding a task, including validation and interaction with the database.
 * It interacts with the TaskDAO and CategoryDAO to persist the task and load categories.
 * It also updates the main application controller.
 *
 * @author
 * Sellami Mohamed Odai
 */
public class AddTaskController {

    private final ObservableList<String> categories = FXCollections.observableArrayList();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final TaskDAO taskDAO = new TaskDAO();

    @FXML private Button importantButton;
    @FXML private ToggleButton lowPriorityButton;
    @FXML private ToggleButton mediumPriorityButton;
    @FXML private ToggleButton highPriorityButton;
    @FXML private TextField taskName;
    @FXML private TextField taskDescription;
    @FXML private DatePicker taskStartDate;
    @FXML private DatePicker taskDueDate;
    @FXML private ComboBox<String> taskCategory;
    @FXML private ComboBox<String> taskReminder;

    private boolean isImportantSelected = false;
    private ToggleGroup priorityGroup;
    private Stage addTaskStage;
    private AppController appController;
    private String userID;

    /**
     * Sets the main application controller.
     *
     * @param appController the main application controller to set
     */
    public void setAppController(AppController appController) {
        this.appController = appController;
    }

    /**
     * Sets the user ID.
     *
     * @param userID the user ID to set
     */
    public void setUserID(String userID) {
        this.userID = userID;
        loadCategories();
    }

    /**
     * Sets the stage for adding a task.
     *
     * @param addTaskStage the stage to set
     */
    public void setAddTaskStage(Stage addTaskStage) {
        this.addTaskStage = addTaskStage;
    }

    /**
     * Initializes the controller.
     * Sets up the priority toggle group, reminder combo box, and date pickers.
     */
    public void initialize() {
        priorityGroup = new ToggleGroup();
        lowPriorityButton.setToggleGroup(priorityGroup);
        mediumPriorityButton.setToggleGroup(priorityGroup);
        highPriorityButton.setToggleGroup(priorityGroup);
        lowPriorityButton.setSelected(true);
        setupReminderComboBox();
        taskReminder.setValue("1 time per week");
        taskCategory.setValue("General");
        setupDatePicker();
    }

    /**
     * Sets up the reminder combo box with predefined values.
     */
    private void setupReminderComboBox() {
        ObservableList<String> reminders = FXCollections.observableArrayList("1 time per week", "3 times per week", "7 times per week");
        taskReminder.setItems(reminders);
    }

    /**
     * Loads the categories from the database and sets them in the combo box.
     */
    private void loadCategories() {
        categories.clear();
        categories.addAll(categoryDAO.loadCategories(userID));
        taskCategory.setItems(categories);
    }

    /**
     * Handles the action of adding a task.
     * Validates the input fields and inserts the task into the database if valid.
     *
     * @throws SQLException if a database access error occurs
     */
    @FXML
    public void handleAddTaskButton() throws SQLException {
        String name = taskName.getText();
        String description = taskDescription.getText();
        LocalDate startDate = taskStartDate.getValue();
        LocalDate dueDate = taskDueDate.getValue();
        String category = taskCategory.getValue();
        String priority = getSelectedPriority();
        String reminderNumber = taskReminder.getValue().split(" ")[0];
        int reminder = Integer.parseInt(reminderNumber);

        StringBuilder errorMessage = getStringBuilder(name, startDate, dueDate);

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
            insertTask(name, description, dueDate, startDate, category, priority, reminder);
            appController.setLatestTaskName(name);
            addTaskStage.close();
        }
    }

    /**
     * Validates the input fields and returns an error message if any field is invalid.
     *
     * @param name the task name
     * @param startDate the task start date
     * @param dueDate the task due date
     * @return a StringBuilder containing the error message
     */
    private static StringBuilder getStringBuilder(String name, LocalDate startDate, LocalDate dueDate) {
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
        return errorMessage;
    }

    /**
     * Inserts a new task into the database.
     *
     * @param name the task name
     * @param description the task description
     * @param dueDate the task due date
     * @param startDate the task start date
     * @param category the task category
     * @param priority the task priority
     * @param reminder the task reminder frequency
     * @throws SQLException if a database access error occurs
     */
    private void insertTask(String name, String description, LocalDate dueDate, LocalDate startDate, String category, String priority, int reminder) throws SQLException {
        taskDAO.createTasks(name, description, dueDate, startDate, startDate.equals(LocalDate.now()) ? Status.Started : Status.Pending, category, priority, userID, isImportantSelected, reminder);
    }

    /**
     * Handles the action of canceling the add task operation.
     * Closes the stage.
     */
    @FXML
    public void handleCancelButton() {
        addTaskStage.close();
    }

    /**
     * Sets up the date pickers with custom cell factories to disable invalid dates.
     */
    public void setupDatePicker() {
        taskDueDate.setDayCellFactory(datePicker -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (item.isBefore(taskStartDate.getValue())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;");
                }
            }
        });
        taskStartDate.setDayCellFactory(datePicker -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (item.isBefore(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;");
                }
            }
        });
        taskStartDate.setValue(LocalDate.now());
    }

    /**
     * Handles the action of toggling the important button.
     * Updates the button style based on the selection state.
     */
    @FXML
    public void handleImportantButton() {
        isImportantSelected = !isImportantSelected;
        if (isImportantSelected) {
            importantButton.getStyleClass().add("important-button-selected");
        } else {
            importantButton.getStyleClass().remove("important-button-selected");
        }
    }

    /**
     * Gets the selected priority from the toggle group.
     *
     * @return the selected priority as a string
     */
    public String getSelectedPriority() {
        ToggleButton selectedButton = (ToggleButton) priorityGroup.getSelectedToggle();
        return selectedButton.getText();
    }
}