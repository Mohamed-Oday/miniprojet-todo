package org.openjfx.miniprojet.controller;

import com.jfoenix.controls.JFXRadioButton;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.geometry.Pos;

import com.jfoenix.controls.JFXListView;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.openjfx.miniprojet.dao.CategoryDAO;
import org.openjfx.miniprojet.dao.NotificationDAO;
import org.openjfx.miniprojet.dao.TaskDAO;
import org.openjfx.miniprojet.model.Notification;
import org.openjfx.miniprojet.model.Status;
import org.openjfx.miniprojet.model.TaskImpl;
import org.openjfx.miniprojet.model.TaskListImpl;
import org.openjfx.miniprojet.utiil.Database;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class Controller {

    @FXML private Label categoryTitle;
    @FXML private JFXListView<TaskImpl> categoryTasks;
    @FXML private AnchorPane displayTasksPane;
    @FXML private AnchorPane importantPane;
    @FXML private Label notificationMessage;
    @FXML private Label notificationTaskName;
    @FXML private AnchorPane notificationForm;
    @FXML private DatePicker TaskDueDateField;
    @FXML private ComboBox<Status> TaskStatusField;
    @FXML private TextField taskDescriptionField;
    @FXML private TextField taskNameField;
    @FXML private AnchorPane myDayPane;
    @FXML private AnchorPane mainPane;
    @FXML private JFXListView<TaskImpl> taskListView;
    @FXML private JFXListView<TaskImpl> taskListView1;
    @FXML private JFXListView<TaskImpl> taskListView2;
    @FXML private JFXListView<String> categoryListView;
    @FXML private JFXListView<String> categoryListView1;
    @FXML private JFXListView<String> categoryListView2;
    @FXML private JFXListView<String> categoryListView21;
    @FXML private Label imageLabel;
    @FXML private Label userNameLabel;
    @FXML private Label imageLabel1;
    @FXML private Label imageLabel11;
    @FXML private Label userNameLabel1;
    @FXML private Label userNameLabel11;
    @FXML private Label imageLabel2;
    @FXML private Label imageLabel21;
    @FXML private Label userNameLabel2;
    @FXML private Label userNameLabel21;
    @FXML private TextField searchField;
    @FXML private TextField searchField1;
    @FXML private TextField searchField2;
    @FXML private TextField searchField3;
    @FXML private Label todayLabel;
    @FXML private AnchorPane editForm;
    @FXML private AnchorPane categoryTasksPane;
    @FXML private VBox mainVbox;
    @FXML private VBox mainVbox1;
    @FXML private VBox mainVbox2;
    @FXML private VBox mainVbox3;
    @FXML private JFXRadioButton high;
    @FXML private JFXRadioButton medium;
    @FXML private JFXRadioButton low;
    @FXML private ToggleGroup priorityGroup;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private MenuButton sortingMenu;
    @FXML private Label infoName;
    @FXML private Label infoDesc;
    @FXML private Label infoDate;
    @FXML private Label infoDueDate;
    @FXML private Label infoStatus;
    @FXML private Label infoPriority;
    @FXML private Label infoCategory;
    @FXML private AnchorPane infoPane;
    @FXML private Label infoComments;
    @FXML private AnchorPane notificationPane;
    @FXML private JFXListView<String> categoryListView3;
    @FXML private JFXListView<Notification> notificationList;
    @FXML private Label notificationNumber;

    private final TaskListImpl tasks = new TaskListImpl(FXCollections.observableArrayList());
    private final TaskDAO taskDAO = new TaskDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();
    private ObservableList<String> categories = FXCollections.observableArrayList();
    private String userID;
    private String latestTaskName;
    private TaskImpl selectedTask;
    private String comment;


    @FXML
    public void initialize() {
        mainPane = myDayPane;
        setupStatusComboBox();
        setupCategoryListViews(categoryListView, categoryListView1, categoryListView2, categoryListView21, categoryListView3);
        setupTaskListViews(taskListView, taskListView1, taskListView2, categoryTasks);
        setupCategoryContextMenu(categoryListView, categoryListView1, categoryListView2, categoryListView21, categoryListView3);
        setupSearchField(searchField, searchField1, searchField2, searchField3);
        setupNotificationButtons(notificationList);
        setupSortingMenu();
        setupPriorityRadioButtons();
        setupTodayLabel();
        loadNotifications();
    }

    @FXML
    private void closeInfoPane() {
        infoPane.setVisible(false);
        mainPane.setDisable(false);
    }

    private void setupStatusComboBox() {
        ObservableList<Status> statusList = FXCollections.observableArrayList(Status.values());
        TaskStatusField.setItems(statusList);
    }

    @SafeVarargs
    private void setupCategoryListViews(JFXListView<String>... listViews) {
        for (JFXListView<String> listView : listViews) {
            listView.setItems(categories);
            listView.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY){
                    String selectedCategory = listView.getSelectionModel().getSelectedItem();
                    if (selectedCategory != null){
                        loadTasksForCategory(selectedCategory);
                    }
                }
            });
        }
    }

    @SafeVarargs
    private void setupTaskListViews(JFXListView<TaskImpl>... listViews) {
        for (JFXListView<TaskImpl> listView : listViews) {
            VBox placeholderLabel = createPlaceholderLabel();
            listView.setPlaceholder(placeholderLabel);
            listView.setItems(tasks.getTasks());
            setupTasksButtons(listView);
            listView.setOnMouseClicked(event -> {
                TaskImpl selectedTask = listView.getSelectionModel().getSelectedItem();
                if (selectedTask != null) {
                    System.out.println(selectedTask.getName());
                    handleEditTask(selectedTask);
                }
            });
        }
    }

    @SafeVarargs
    private void setupCategoryContextMenu(JFXListView<String>... listViews){
        for (JFXListView<String> listView : listViews) {
            ContextMenu contextMenu = new ContextMenu();
            MenuItem deleteCategoryItem = createMenuItem("Delete Category", event -> deleteSelectedCategory());
            MenuItem deleteCategoryWithTasksItem = createMenuItem("Delete Category and Tasks", event -> deleteCategoryWithTasks());
            contextMenu.getItems().addAll(deleteCategoryItem, deleteCategoryWithTasksItem);
            listView.setContextMenu(contextMenu);
        }
    }

    private void setupSearchField(TextField... searchFields) {
        FilteredList<TaskImpl> filteredList = new FilteredList<>(tasks.getTasks(), task -> true);
        for (TextField searchField : searchFields) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> filteredList.setPredicate(task -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return task.getName().toLowerCase().contains(lowerCaseFilter)
                        || task.getDescription().toLowerCase().contains(lowerCaseFilter);
            }));
        }
        setListViewItems(filteredList, taskListView, taskListView1, taskListView2, categoryTasks);
    }

    private void setupSortingMenu(){
        MenuItem originalSort = createMenuItem("Original Order", event -> sortTasks(null));
        MenuItem sortByPriority = createMenuItem("Sort by Priority", event -> sortTasks(tasks.sortTasksByPriority()));
        sortingMenu.getItems().addAll(originalSort, sortByPriority);
    }

    private void setupPriorityRadioButtons() {
        priorityGroup = new ToggleGroup();
        high.setToggleGroup(priorityGroup);
        medium.setToggleGroup(priorityGroup);
        low.setToggleGroup(priorityGroup);
    }

    private void setupTodayLabel() {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd");
        todayLabel.setText(currentDate.format(formatter));
    }

    private void sortTasks(ObservableList<TaskImpl> sortedTasks) {
        if (sortedTasks == null) {
            sortedTasks = tasks.getTasks();
        }
        setListViewItems(sortedTasks, taskListView, taskListView1, taskListView2, categoryTasks);
    }

    @SafeVarargs
    private void setListViewItems(ObservableList<TaskImpl> tasks, JFXListView<TaskImpl>... listViews) {
        for (JFXListView<TaskImpl> listView : listViews) {
            listView.setItems(tasks);
            listView.refresh();
        }
    }

    private void setupTasksButtons(ListView<TaskImpl> listView) {
        listView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(TaskImpl task, boolean empty) {
                super.updateItem(task, empty);
                if (empty || task == null) {
                    setGraphic(null);
                    setText(null);
                    setStyle("-fx-background-color: transparent;");
                    setOnMouseEntered(null);
                    setOnMouseExited(null);
                } else {
                    HBox container = createTaskContainer(task);
                    setGraphic(container);
                    setText("");
                    setPrefHeight(Region.USE_COMPUTED_SIZE);
                    setStyle("-fx-background-color: #37393a; -fx-cursor: hand;");
                    setOnMouseEntered(event -> setStyle("-fx-background-color: #303030; -fx-cursor: hand;"));
                    setOnMouseExited(event -> setStyle("-fx-background-color: #37393a; -fx-cursor: hand;"));
                }
            }
        });
    }

    private void setupNotificationButtons(ListView<Notification> listView) {
        listView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Notification notification, boolean empty) {
                super.updateItem(notification, empty);
                if (empty || notification == null) {
                    setGraphic(null);
                    setText(null);
                    setStyle("-fx-background-color: transparent;");
                    setOnMouseEntered(null);
                    setOnMouseExited(null);
                } else {
                    HBox container = createNotificationContainer(notification);
                    setGraphic(container);
                    setText("");
                    setPrefHeight(Region.USE_COMPUTED_SIZE);
                    setStyle("-fx-background-color: #37393a; -fx-cursor: hand;");
                    setOnMouseEntered(event -> setStyle("-fx-background-color: #303030; -fx-cursor: hand;"));
                    setOnMouseExited(event -> setStyle("-fx-background-color: #37393a; -fx-cursor: hand;"));
                }
            }
        });
    }

    private HBox createNotificationContainer(Notification notification){
        HBox container = new HBox();
        container.setSpacing(5);
        Label messageLabel = createLabel(notification.getTitle(), "-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
        messageLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(messageLabel, Priority.ALWAYS);
        Label dateLabel = createLabel(notification.getTime().toString(), "-fx-text-fill: #f3b807; -fx-font-size: 14px; -fx-font-weight: bold;");
        Button markAsReadButton = createButton("✔", Font.font("System Bold", FontWeight.BOLD, 12), "#00FF7F", event -> {
            notificationDAO.markAsRead(notification.getId());
            loadNotifications();
        });
        container.getChildren().addAll(messageLabel, dateLabel, markAsReadButton);
        HBox.setMargin(markAsReadButton, new Insets(0, 0, 0, 10));
        container.setAlignment(Pos.CENTER_LEFT);
        return container;
    }

    private HBox createTaskContainer(TaskImpl task) {
        HBox container = new HBox();
        container.setSpacing(5);
        Label nameLabel = createLabel(task.getName(), "-fx-text-fill: white; -fx-font-size: 14px;");
        nameLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(nameLabel, Priority.ALWAYS);
        Label priorityLabel = createPriorityLabel(task);
        Label statusLabel = createStatusLabel(task);
        Button deleteButton = createButton("✖", Font.font("System Bold", FontWeight.BOLD, 12), "#FF6F61", event -> {
            deleteTask(task);
            refreshListViews(taskListView, taskListView1, taskListView2, categoryTasks);
            tasks.deleteTask(task);
        });
        Button checkButton = createButton(task.getStatus().equals(Status.Completed) ? "✔" : "", Font.font("System Bold", FontWeight.BOLD, 12), "#00FF7F", event -> {
            if (task.getStatus().equals(Status.Completed)) {
                task.changeStatus(Status.Started);
                updateTask(task);
                return;
            }
            task.changeStatus(Status.Completed);
            updateTask(task);
            refreshListViews(taskListView, taskListView1, taskListView2, categoryTasks);
            showNotification("Task completed successfully.", "Task", "has been completed", task.getName());
        });
        Button editButton = createButton("✎", Font.font("System Bold", FontWeight.BOLD, 20), "#FFD700", event -> handleEditTask(task));
        Button infoButton = createButton("\uD835\uDCF2", Font.font("System Bold", FontWeight.BOLD, 16), "#00FF7F", event -> {
            displayTaskInfo(task);
            mainPane.setDisable(true);
            infoPane.setVisible(true);
        });
        Button commentButton = createButton("\uD83D\uDCAC", Font.font("System Bold", FontWeight.BOLD, 16), "#FFD700", event -> {
            try {
                handleCommentButton(task);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        Button importantButton = createButton(task.isImportant() ? "★" : "☆", Font.font("System Bold", FontWeight.BOLD, 16), "#FF6F61", event -> {
            task.setImportant(!task.isImportant());
            updateTask(task);
            refreshListViews(taskListView, taskListView1, taskListView2, categoryTasks);
            showNotification("Task updated successfully.", "Task", "has been updated", task.getName());
        });
        HBox.setMargin(importantButton, new Insets(0, 5, 0, 0));
        HBox.setMargin(commentButton, new Insets(0, 5, 0, 0));
        HBox.setMargin(infoButton, new Insets(0, 5, 0, 0));
        HBox.setMargin(editButton, new Insets(0, 5, 0, 0));
        HBox.setMargin(checkButton, new Insets(0, 5, 0, 0));
        HBox.setMargin(statusLabel, new Insets(0, 15, 0, 0));
        container.getChildren().addAll(checkButton, nameLabel, statusLabel, priorityLabel, importantButton, commentButton, infoButton, editButton, deleteButton);
        container.setAlignment(Pos.CENTER_LEFT);
        return container;
    }

    private void handleCommentButton(TaskImpl task) throws IOException{
        showAddCommentDialog(task);
    }

    private Label createLabel(String text, String style) {
        Label label = new Label(text);
        label.setStyle(style);
        return label;
    }

    private Label createPriorityLabel(TaskImpl task) {
        String priority = task.getPriority();
        String style = switch (priority) {
            case "High" -> "-fx-text-fill: #FF6F61; -fx-font-size: 14px; -fx-font-weight: bold;";
            case "Medium" -> "-fx-text-fill: #FFD700; -fx-font-size: 14px; -fx-font-weight: bold;";
            default -> "-fx-text-fill: #00FF7F; -fx-font-size: 14px; -fx-font-weight: bold;";
        };
        Label priorityLabel = createLabel(priority, style);
        priorityLabel.setPrefWidth(80);
        return priorityLabel;
    }

    private Label createStatusLabel(TaskImpl task) {
        String status = task.getStatus().toString();
        String style = switch (status) {
            case "Completed" -> "-fx-text-fill: #00FF7F; -fx-font-size: 14px; -fx-font-weight: bold;";
            case "Started" -> "-fx-text-fill: #FFD700; -fx-font-size: 14px; -fx-font-weight: bold;";
            default -> "-fx-text-fill: #FF6F61; -fx-font-size: 14px; -fx-font-weight: bold;";
        };
        Label statusLabel = createLabel(status, style);
        statusLabel.setPrefWidth(80);
        return statusLabel;
    }

    private Button createButton(String iconText, Font font, String hoverColor, EventHandler<ActionEvent> action) {
        Button button = new Button();
        button.setPrefSize(30, 30);
        button.setMaxSize(30, 30);
        Text icon = new Text(iconText);
        icon.setFont(font);
        icon.setFill(Color.WHITE);
        StackPane iconContainer = new StackPane(icon);
        iconContainer.setAlignment(Pos.CENTER);
        button.setGraphic(iconContainer);
        button.setStyle("-fx-background-color: transparent; -fx-border-radius: 50%; -fx-background-radius: 50%; -fx-padding: 0; -fx-border-width: 2; -fx-cursor: hand; -fx-border-color: #d4d5d5;");
        button.setOnMouseEntered(event -> button.setStyle("-fx-background-color: transparent; -fx-border-radius: 50%; -fx-background-radius: 50%; -fx-padding: 0; -fx-border-width: 2 2 2 2; -fx-border-color: " + hoverColor));
        button.setOnMouseExited(event -> button.setStyle("-fx-background-color: transparent; -fx-border-radius: 50%; -fx-background-radius: 50%; -fx-padding: 0; -fx-border-width: 2 2 2 2; -fx-border-color: #d4d5d5"));
        button.setOnAction(action);
        return button;
    }

    private MenuItem createMenuItem(String text, EventHandler<ActionEvent> action) {
        MenuItem menuItem = new MenuItem(text);
        menuItem.setOnAction(action);
        return menuItem;
    }

    private VBox createPlaceholderLabel() {
        Label placeholderLabel = new Label("Add your first task!");
        placeholderLabel.setId("noTaskLabel");
        Image image = new Image(Objects.requireNonNull(getClass().getResource("/org/openjfx/miniprojet/assets/images/AddNotes-pana-2x.png")).toString());
        ImageView placeholderImage = new ImageView(image);
        VBox placeholderContainer = new VBox(placeholderLabel, placeholderImage);
        placeholderContainer.setAlignment(Pos.CENTER);
        return placeholderContainer;
    }

    private void deleteSelectedCategory(){
        String selectedCategory = getCurrentCategory();
        if ("General".equals(selectedCategory)) {
            alert("General category cannot be deleted.");
            return;
        }
        deleteCategory(selectedCategory, false);
    }

    private void deleteCategoryWithTasks() {
        String selectedCategory = getCurrentCategory();
        if ("General".equals(selectedCategory)) {
            alert("General category cannot be deleted.");
            return;
        }
        deleteCategory(selectedCategory, true);
    }

    private String getCurrentCategory() {
        if (myDayPane.isVisible()) {
            return categoryListView.getSelectionModel().getSelectedItem();
        } else if (importantPane.isVisible()) {
            return categoryListView1.getSelectionModel().getSelectedItem();
        } else if (displayTasksPane.isVisible()) {
            return categoryListView2.getSelectionModel().getSelectedItem();
        } else if (notificationPane.isVisible()){
            return categoryListView3.getSelectionModel().getSelectedItem();
        } else {
            return categoryListView21.getSelectionModel().getSelectedItem();
        }
    }

    private void deleteCategory(String categoryName, boolean deleteTasks) {
        categoryDAO.deleteCategory(categoryName, userID, deleteTasks);
        if (categoryTasksPane.isVisible()) {
            switchToMyDayPane();
        }
        loadTasks();
        loadCategories();
        clearSelection(categoryListView, categoryListView1, categoryListView2, categoryListView21, categoryListView3);
    }

    @SafeVarargs
    private void clearSelection(ListView<String>... listViews) {
        for (ListView<String> listView : listViews) {
            listView.getSelectionModel().clearSelection();
        }
    }

    private void switchToMyDayPane() {
        categoryTasksPane.setVisible(false);
        myDayPane.setVisible(true);
        mainPane = myDayPane;
    }

    private void alert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadTasks() {
        tasks.setTasks(taskDAO.loadTasks(userID, mainPane, categoryTitle.getText()));
    }

    private void loadTasksForCategory(String categoryName) {
        clearSearch(searchField, searchField1, searchField2, searchField3);
        if (editForm.isVisible()) {
            handleCancelButton();
        }
        switchToCategoryTasksPane(categoryName);
        loadTasksByCategory(categoryName);
    }

    private void loadTasksByCategory(String categoryName) {
        tasks.setTasks(taskDAO.loadTasksByCategory(categoryName, userID));
    }

    private void switchToCategoryTasksPane(String categoryName) {
        categoryTasksPane.setVisible(true);
        myDayPane.setVisible(false);
        displayTasksPane.setVisible(false);
        importantPane.setVisible(false);
        notificationPane.setVisible(false);
        categoryTitle.setText(categoryName);
        mainPane = categoryTasksPane;
    }

    private void clearSearch(TextField... searchFields){
        for (TextField searchField : searchFields){
            searchField.clear();
        }
    }

    public void handleEditTask(TaskImpl task) {
        TaskStatusField.setValue(task.getStatus());
        TranslateTransition slider = new TranslateTransition(Duration.seconds(0), editForm);
        slider.setToX(0);
        slider.setOnFinished(event -> {
            resizeMainPaneForEdit();
            editForm.setVisible(true);
            populateEditForm(task);
        });
        slider.play();
        this.selectedTask = task;
    }

    private void populateEditForm(TaskImpl task) {
        taskNameField.setText(task.getName());
        taskDescriptionField.setText(task.getDescription());
        TaskDueDateField.setValue(task.getDueDate());
        TaskStatusField.setValue(task.getStatus());
        priorityGroup.selectToggle(getPriorityToggle(task.getPriority()));
        categoryComboBox.setValue(task.getCategory());
        showEditFormSearchField(false, searchField, searchField1, searchField2, searchField3);
        mainVbox.setPadding(new Insets(0, 320, 0, 0));
        mainVbox1.setPadding(new Insets(0, 320, 0, 0));
        mainVbox2.setPadding(new Insets(0, 320, 0, 0));
        mainVbox3.setPadding(new Insets(0, 320, 0, 0));
    }

    private JFXRadioButton getPriorityToggle(String priority){
        return switch (priority) {
            case "High" -> high;
            case "Medium" -> medium;
            case "Low" -> low;
            default -> null;
        };
    }

    private void resizeMainPaneForEdit() {
        if (mainPane != null) {
            mainPane.setPrefWidth(850);
            mainPane.applyCss();
            mainPane.layout();
        }
    }

    @FXML
    public void handleSaveButton() {
        if (selectedTask != null) {
            updateTaskFields(selectedTask);
            updateTask(selectedTask);
            showNotification("Task updated successfully.", "Task", "has been updated", selectedTask.getName());
            handleCancelButton();
        }
    }

    private void updateTaskFields(TaskImpl task) {
        String taskName = taskNameField.getText();
        String taskDescription = taskDescriptionField.getText();
        LocalDate taskDueDate = TaskDueDateField.getValue();
        Status taskStatus = TaskStatusField.getValue();
        String categoryName = categoryComboBox.getValue();
        String taskPriority = ((JFXRadioButton) priorityGroup.getSelectedToggle()).getText();
        task.editTask(taskName, taskDescription, taskDueDate, taskStatus, taskPriority, categoryName);
    }

    @FXML
    public void handleCancelButton() {
        editForm.setVisible(false);
        resetMainPaneSize();
        hideEditFormVBox(mainVbox, mainVbox1, mainVbox2, mainVbox3);
        showEditFormSearchField(true, searchField, searchField1, searchField2, searchField3);
    }

    @FXML
    public void handleDeleteButton() {
        deleteTask(selectedTask);
        tasks.deleteTask(selectedTask);
        handleCancelButton();
    }

    private void hideEditFormVBox(VBox... mainVbox){
        for (VBox vbox : mainVbox){
            vbox.setPadding(new Insets(0, 0, 0, 0));
            vbox.applyCss();
            vbox.layout();
        }
    }

    private void showEditFormSearchField(boolean show, TextField... searchFields) {
        for (TextField searchField : searchFields) {
            searchField.setVisible(show);
        }
        if (!show) {
            mainVbox.setPadding(new Insets(0, 320, 0, 0));
        } else {
            mainVbox.setPadding(new Insets(0, 0, 0, 0));
        }
        mainVbox.applyCss();
        mainVbox.layout();
    }

    private void resetMainPaneSize() {
        if (mainPane != null) {
            mainPane.setPrefWidth(1200);
            mainPane.setPadding(new Insets(0, 0, 0, 0));
            mainPane.applyCss();
            mainPane.layout();
        }
    }

    @FXML
    public void handleSignOutButton(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/openjfx/miniprojet/assets/fxml/EntryPage.fxml"));
        Parent root = loader.load();
        try {
            Database.getInstance().deleteSavedUser();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("ToDo App");
        stage.show();
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.close();
    }

    public void deleteTask(TaskImpl task) {
        taskDAO.deleteTask(task);
        setLatestTaskName(task.getName());
        showNotification("Task deleted successfully.", "Task", "has been deleted", latestTaskName);
    }

    public void updateTask(TaskImpl task) {
        taskDAO.updateTask(task, userID);
        loadTasks();
    }

    public void loadCategories() {
        categories.clear();
        categories = categoryDAO.loadCategories(userID);
        categoryComboBox.setItems(categories);
        setCategoryViewItems(categories, categoryListView, categoryListView1, categoryListView2, categoryListView21, categoryListView3);
    }

    @SafeVarargs
    @FXML
    private void setCategoryViewItems(ObservableList<String> categories, ListView<String>... listView) {
        for (ListView<String> list : listView) {
            list.setItems(categories);
        }
    }

    public void showNotification(String message, String part1, String part2, String taskName) {
        if (taskName != null && !taskName.isEmpty()) {
            notificationDAO.insertNotification(userID, part1 + " " + taskName + " " + part2, message);
            notificationMessage.setText(message);
            notificationTaskName.setText(part1 + " \"" + taskName + "\" " + part2);
            notificationForm.setVisible(true);
            notificationForm.setTranslateX(300);
            notificationForm.setTranslateY(0);
            TranslateTransition sliderIn = new TranslateTransition(Duration.seconds(0.5), notificationForm);
            sliderIn.setToX(0);
            sliderIn.setOnFinished(e -> {
                PauseTransition pause = new PauseTransition(Duration.seconds(3));
                pause.setOnFinished(event -> {
                    TranslateTransition sliderOut = new TranslateTransition(Duration.seconds(0.5), notificationForm);
                    sliderOut.setToX(400);
                    sliderOut.setOnFinished(evt -> notificationForm.setVisible(false));
                    sliderOut.play();
                });
                pause.play();
            });
            sliderIn.play();
            latestTaskName = null;
        }
        loadNotifications();
    }

    @FXML
    public void loadNotifications() {
        List<Notification> notifications = notificationDAO.getNotificationsByUser(userID);
        notificationNumber.setText(String.valueOf(notifications.size()));
        notificationList.setItems(FXCollections.observableArrayList(notifications));
    }

    @FXML
    public void handleMyDayButton() {
        handleChangePane(myDayPane);
    }

    @FXML
    public void handleImportantButton() {
        handleChangePane(importantPane);
    }

    @FXML
    public void handleDisplayTasksButton() {
        handleChangePane(displayTasksPane);
    }

    @FXML
    public void handleDisplayNotificationButton() {
        handleChangePane(notificationPane);
    }

    private void handleChangePane(AnchorPane pane) {
        clearSearch(searchField, searchField1, searchField2, searchField3);
        resetMainPaneSize();
        if (editForm.isVisible()) {
            handleCancelButton();
        }
        switchToPane(pane);
        loadTasks();
        loadCategories();
        loadNotifications();
        refreshListViews(taskListView, taskListView1, taskListView2, categoryTasks);
    }

    private void switchToPane(AnchorPane pane) {
        myDayPane.setVisible(false);
        displayTasksPane.setVisible(false);
        importantPane.setVisible(false);
        categoryTasksPane.setVisible(false);
        notificationPane.setVisible(false);
        pane.setVisible(true);
        mainPane = pane;
    }

    @SafeVarargs
    private void refreshListViews(ListView<TaskImpl>... listViews) {
        for (ListView<TaskImpl> listView : listViews) {
            listView.refresh();
        }
    }

    @FXML
    public void handleAddButton() throws IOException {
        showAddTaskDialog();
        showNotification("Task added successfully.", "Task", "has been added", latestTaskName);
        loadTasks();
    }

    @FXML
    public void handleAddCategory() throws IOException {
        showAddCategoryDialog();
    }

    private void showAddCommentDialog(TaskImpl task) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/openjfx/miniprojet/assets/fxml/addComment.fxml"));
        Parent root = loader.load();
        AddCommentController addCommentController = loader.getController();
        Stage addCommentStage = new Stage();
        addCommentStage.setTitle("Add Comment");
        addCommentStage.setScene(new Scene(root));
        addCommentStage.initStyle(StageStyle.UTILITY);
        addCommentStage.initModality(Modality.APPLICATION_MODAL);
        addCommentController.setUserID(userID);
        addCommentController.setTask(task);
        addCommentController.setMainController(this);
        addCommentController.setAddCommentStage(addCommentStage);
        addCommentStage.showAndWait();
    }

    private void showAddTaskDialog() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/openjfx/miniprojet/assets/fxml/addTask.fxml"));
        Parent root = loader.load();
        addTaskController addTaskController = loader.getController();
        Stage addTaskStage = new Stage();
        addTaskStage.setTitle("Add Task");
        addTaskStage.setScene(new Scene(root));
        addTaskStage.initStyle(StageStyle.UTILITY);
        addTaskStage.initModality(Modality.APPLICATION_MODAL);
        addTaskController.setAddTaskStage(addTaskStage);
        addTaskController.setMainController(this);
        addTaskController.setUserID(userID);
        addTaskStage.showAndWait();
    }

    private void showAddCategoryDialog() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/openjfx/miniprojet/assets/fxml/addCategory.fxml"));
        Parent root = loader.load();
        AddCategoryController addCategoryController = loader.getController();
        Stage addCategoryStage = new Stage();
        addCategoryStage.setTitle("Add Category");
        addCategoryStage.setScene(new Scene(root));
        addCategoryStage.initStyle(StageStyle.UTILITY);
        addCategoryStage.initModality(Modality.APPLICATION_MODAL);
        addCategoryController.setAddCategoryStage(addCategoryStage);
        addCategoryController.setMainController(this);
        addCategoryController.setUserID(userID);
        addCategoryStage.showAndWait();
    }

    public void setLatestTaskName(String latestTaskName) {
        this.latestTaskName = latestTaskName;
    }

    public void setUserName(String userName) {
        this.userID = userName;
        setupUser(userNameLabel, userNameLabel1, userNameLabel2, userNameLabel21, userNameLabel11);
        setupUserImage(imageLabel, imageLabel1, imageLabel2, imageLabel21, imageLabel11);
        loadTasks();
        loadNotifications();
        loadCategories();
    }

    private void setupUser(Label... usernameLabels) {
        for (Label usernameLabel : usernameLabels) {
            usernameLabel.setText(userID);
        }
    }

    private void setupUserImage(Label... userImage) {
        String initials = userID.substring(0, 2).toUpperCase();
        for (Label imageLabel : userImage) {
            imageLabel.setText(initials);
        }
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    private void displayTaskInfo(TaskImpl task) {
        infoPane.setVisible(true);
        infoName.setText(task.getName());
        infoDesc.setText(task.getDescription());

        List<String> comments = task.getComments();
        if (comments != null && !comments.isEmpty()) {
            StringBuilder formattedComments = new StringBuilder();
            for (int i = 0; i < comments.size(); i++) {
                formattedComments.append("• ").append(comments.get(i));
                if (i < comments.size() - 1) {
                    formattedComments.append("\n\n");
                }
            }
            infoComments.setText(formattedComments.toString());
        } else {
            infoComments.setText("No comments yet");
        }
        
        infoDate.setText(task.getStartDate().toString());
        infoDueDate.setText(task.getDueDate().toString());
        infoStatus.setText(task.getStatus().toString());
        infoStatus.getStyleClass().clear();
        infoStatus.getStyleClass().addAll("status-chip", "status-" + task.getStatus().toString().toLowerCase());
        
        infoPriority.setText(task.getPriority());
        infoPriority.getStyleClass().clear();
        infoPriority.getStyleClass().addAll("priority-chip", "priority-" + task.getPriority().toLowerCase());
        
        infoCategory.setText(task.getCategory());
    }

}