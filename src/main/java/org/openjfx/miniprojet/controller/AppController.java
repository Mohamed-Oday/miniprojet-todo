    package org.openjfx.miniprojet.controller;

    import com.jfoenix.controls.JFXButton;
    import com.jfoenix.controls.JFXListView;
    import com.jfoenix.controls.JFXRadioButton;
    import javafx.animation.PauseTransition;
    import javafx.animation.TranslateTransition;
    import javafx.application.Platform;
    import javafx.collections.FXCollections;
    import javafx.collections.ListChangeListener;
    import javafx.collections.ObservableList;
    import javafx.collections.transformation.FilteredList;
    import javafx.event.ActionEvent;
    import javafx.event.EventHandler;
    import javafx.fxml.FXML;
    import javafx.fxml.FXMLLoader;
    import javafx.geometry.Insets;
    import javafx.geometry.Pos;
    import javafx.scene.Node;
    import javafx.scene.Parent;
    import javafx.scene.Scene;
    import javafx.scene.chart.LineChart;
    import javafx.scene.chart.XYChart;
    import javafx.scene.control.*;
    import javafx.scene.image.Image;
    import javafx.scene.image.ImageView;
    import javafx.scene.input.MouseButton;
    import javafx.scene.layout.*;
    import javafx.scene.text.Text;
    import javafx.stage.FileChooser;
    import javafx.stage.Modality;
    import javafx.stage.Stage;
    import javafx.stage.StageStyle;
    import javafx.util.Duration;
    import org.openjfx.miniprojet.dao.*;
    import org.openjfx.miniprojet.model.*;

    import java.io.File;
    import java.io.IOException;
    import java.sql.SQLException;
    import java.time.LocalDate;
    import java.util.*;

    public class AppController {

        private static final String IMAGE_PATH = "/org/openjfx/miniprojet/assets/images/";
        private static final String SELECTED_PAGE_CLASS = "selected-page";
        private static final String PAGE_BACKGROUND_CLASS = "page-background";
        private static final String MYDAY_BACKGROUND_CLASS = "myDay-background";

        private final CollaborationDAO collaborationDAO = new CollaborationDAO();
        private final NotificationDAO notificationDAO = new NotificationDAO();
        private final CategoryDAO categoryDAO = new CategoryDAO();
        private final TaskDAO taskDAO = new TaskDAO();
        private final UserDAO userDAO = new UserDAO();

        private final NotificationSoundController notificationSoundController = new NotificationSoundController();
        private final TaskListImpl tasks = new TaskListImpl(FXCollections.observableArrayList());

        @FXML private JFXButton myDayButton;
        @FXML private JFXButton importantButton;
        @FXML private JFXButton tasksButton;
        @FXML private JFXButton notificationButton;
        @FXML private VBox page;
        @FXML private JFXButton selectedPage;
        @FXML private ImageView pageIcon;
        @FXML private Label user;
        @FXML private Label imageLabel;
        @FXML private Label pageTitle;
        @FXML private JFXListView<String> categoryListView;
        @FXML private JFXListView<TaskImpl> taskListView;
        @FXML private JFXListView<Notification> notificationListView;
        @FXML private VBox notificationPage;
        @FXML private TextField searchField;
        @FXML private AnchorPane editForm;
        @FXML private TextField taskNameField;
        @FXML private TextField taskDescriptionField;
        @FXML private DatePicker taskStartDateField;
        @FXML private DatePicker TaskDueDateField;
        @FXML private ToggleGroup priorityGroup;
        @FXML private ComboBox<String> categoryComboBox;
        @FXML private ComboBox<Status> statusComboBox;
        @FXML private JFXRadioButton high;
        @FXML private JFXRadioButton medium;
        @FXML private JFXRadioButton low;
        @FXML private AnchorPane notificationForm;
        @FXML private Label notificationMessage;
        @FXML private Label notificationTaskName;
        @FXML private AnchorPane infoPane;
        @FXML private BorderPane mainPane;
        @FXML private Label infoName;
        @FXML private Label infoDesc;
        @FXML private Label infoComments;
        @FXML private Label infoDate;
        @FXML private Label infoDueDate;
        @FXML private Label infoStatus;
        @FXML private Label infoPriority;
        @FXML private Label infoCategory;
        @FXML private MenuButton categoryMenu;
        @FXML private MenuButton priorityMenu;
        @FXML private MenuButton statusMenu;
        @FXML private AnchorPane profilePane;
        @FXML private Text profileName;
        @FXML private Text totalTasks;
        @FXML private Text completedTasks;
        @FXML private Text abandonedTasks;
        @FXML private Text deletedTasks;
        @FXML private Text completionRate;
        @FXML private LineChart<String, Number> weeklyActivityChart;
        @FXML private JFXListView<String> profileCategories;
        @FXML private ComboBox<String> taskReminder;
        @FXML private MenuButton dateMenu;

        private boolean isTaskCompleted;
        private boolean isTaskAbandoned;
        private Status latestStatus;
        private String userID;
        private String latestTaskName;
        private ObservableList<String> categories = FXCollections.observableArrayList();
        private TaskImpl selectedTask;
        private String comment;
        private ObservableList<TaskImpl> currentTasks;
        private String selectedCategory = "All";
        private String selectedPriority = "All";
        private String selectedDate = "All";
        private Status selectedStatus = null;
        private TaskReminderService reminderService;

        public void initialize() {
            selectedPage = myDayButton;
            selectedCategory = "All";
            selectedPriority = "All";
            selectedStatus = null;
            setupStatusComboBox();
            setupReminderComboBox();
            setupCategoryListView();
            setupTaskListView();
            setupCategoryContextMenu();
            setupSearchField();
            applyFilters();
            setupNotificationButtons();
            setupCategoryButtons();
            setupProfileCategories();
            setupSortingMenu();
            loadNotifications();
            Platform.runLater(this::checkUpcomingTasks);
            Platform.runLater(this::checkOverdueTasks);
            Platform.runLater(() -> {
                if (collaborationDAO.hasInvites(userID)) {
                    ObservableList<String> pendingInvites = collaborationDAO.getPendingInvites(userID);
                    pendingInvitesAlert(pendingInvites);
                }
            });
        }

        private void setupReminderComboBox() {
            ObservableList<String> reminders = FXCollections.observableArrayList("1 time per week", "3 times per week", "7 times per week");
            taskReminder.setItems(reminders);
        }

        private void setupWeeklyActivityChart() {
            weeklyActivityChart.getData().clear();
            XYChart.Series<String, Number> completedSeries = new XYChart.Series<>();
            completedSeries.setName("Completed Tasks");


            Map<String, Integer> completedData = taskDAO.getLastSevenDaysData(userID);

            completedData.forEach((date, value) -> completedSeries.getData().add(new XYChart.Data<>(date, value)));

            weeklyActivityChart.setCreateSymbols(true);
            weeklyActivityChart.setAnimated(true);
            weeklyActivityChart.getStyleClass().add("weekly-chart");

            weeklyActivityChart.getData().addAll(completedSeries);

            for (XYChart.Series<String, Number> series : weeklyActivityChart.getData()) {
                for (XYChart.Data<String, Number> data : series.getData()) {
                    data.getNode().setOnMouseEntered(event -> data.getNode().setStyle("-fx-background-color: #ffffff; -fx-background-radius: 5px;"));
                    data.getNode().setOnMouseExited(event -> data.getNode().setStyle(""));
                }
            }
        }

        @FXML
        private void closeInfoPane() {
            infoPane.setVisible(false);
            mainPane.setDisable(false);
        }

        private void setupCategoryContextMenu() {
            ContextMenu contextMenu = new ContextMenu();

            MenuItem deleteCategoryItem = createMenuItem("Delete Category", event -> {
                try {
                    deleteSelectedCategory();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            MenuItem deleteCategoryWithTasksItem = createMenuItem("Delete Category and Tasks", event -> {
                try {
                    deleteCategoryWithTasks();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });

            contextMenu.getStyleClass().add("context-menu");
            contextMenu.getItems().addAll(deleteCategoryItem, deleteCategoryWithTasksItem);

            categoryListView.setContextMenu(contextMenu);
        }

        private void setupSortingMenu(){
            getCategoryMenu();
            getPriorityMenu();
            getStatusMenu();
            getDateMenu();
        }

        private void getDateMenu(){
            MenuItem allDates = createMenuItem("All", event -> {
                selectedDate = "All";
                applyFilters();
                statusMenu.setText("Dates");
            });

            MenuItem dueDate = createMenuItem("Due Date", event -> {
                selectedDate = "Due Date";
                applyFilters();
                statusMenu.setText("Dates");
            });

            MenuItem startDate = createMenuItem("Start Date", event -> {
                selectedDate = "Start Date";
                applyFilters();
                statusMenu.setText("Dates");
            });
            dateMenu.getItems().addAll(allDates, dueDate, startDate);
        }

        private void getStatusMenu() {
            MenuItem allStatus = createMenuItem("All", event -> {
                selectedStatus = null;
                applyFilters();
                statusMenu.setText("Status");
            });
            statusMenu.getItems().add(allStatus);
            List<Status> statusList = statusComboBox.getItems();
            for (Status status : statusList) {
                MenuItem statusItem = createMenuItem(status.toString(), event -> {
                    selectedStatus = status;
                    applyFilters();
                    statusMenu.setText(status.toString());
                });
                statusMenu.getItems().add(statusItem);
            }
        }

        private void getPriorityMenu() {
            MenuItem allPriority = createMenuItem("All", event -> {
                selectedPriority = "All";
                applyFilters();
                priorityMenu.setText("Priority");
            });
            priorityMenu.getItems().add(allPriority);
            List<String> priorityList = List.of("High", "Medium", "Low");
            for (String priority : priorityList) {
                MenuItem priorityItem = createMenuItem(priority, event -> {
                    selectedPriority = priority;
                    applyFilters();
                    priorityMenu.setText(priority);
                });
                priorityMenu.getItems().add(priorityItem);
            }
        }

        private void getCategoryMenu(){
            List<String> categoryList = categoryListView.getItems();
            MenuItem allCategory = createMenuItem("All", event -> {
                selectedCategory = "All";
                applyFilters();
                categoryMenu.setText("Category");
            });
            categoryMenu.getItems().add(allCategory);
            for (String category : categoryList) {
                MenuItem categoryItem = createMenuItem(category, event -> {
                    selectedCategory = category;
                    applyFilters();
                    categoryMenu.setText(category);
                });
                categoryMenu.getItems().add(categoryItem);
            }
        }

        private void applyFilters() {
            ObservableList<TaskImpl> filteredTasks = FXCollections.observableArrayList();
            // Start with all tasks
            filteredTasks.addAll(tasks.getTasks());
            // Backup of the original order
            ObservableList<TaskImpl> originalOrder = FXCollections.observableArrayList(filteredTasks);

            // Apply category filter
            if (!selectedCategory.equals("All")) {
                filteredTasks.removeIf(task -> !task.getCategory().equals(selectedCategory));
                originalOrder.removeIf(task -> !task.getCategory().equals(selectedCategory));
            }
            // Apply priority filter
            if (!selectedPriority.equals("All")) {
                filteredTasks.removeIf(task -> !task.getPriority().equals(selectedPriority));
                originalOrder.removeIf(task -> !task.getPriority().equals(selectedPriority));
            }
            // Apply status filter
            if (selectedStatus != null) {
                filteredTasks.removeIf(task -> !task.getStatus().equals(selectedStatus));
                originalOrder.removeIf(task -> !task.getStatus().equals(selectedStatus));
            }
            // Apply date filter
            // Sort by due date
            if (selectedDate.equals("Due Date")) {
                filteredTasks.sort(Comparator.comparing(TaskImpl::getDueDate));
            }
            // Sort by start date
            if (selectedDate.equals("Start Date")) {
                filteredTasks.sort(Comparator.comparing(TaskImpl::getStartDate));
            }
            // If no date filter is selected, restore the original order
            if (selectedDate.equals("All")) {
                filteredTasks.setAll(originalOrder);
            }

            // Update currentTasks with the filtered list
            currentTasks.setAll(filteredTasks);
        }



        private MenuItem createMenuItem(String text, EventHandler<ActionEvent> action) {
            MenuItem menuItem = new MenuItem(text);
            menuItem.setOnAction(action);
            return menuItem;
        }

        private void deleteSelectedCategory() throws SQLException {
            String selectedCategory = categoryListView.getSelectionModel().getSelectedItem();
            if ("General".equals(selectedCategory)) {
                alert();
                return;
            }
            deleteCategory(selectedCategory, false);
        }

        private void deleteCategoryWithTasks() throws SQLException {
            String selectedCategory = categoryListView.getSelectionModel().getSelectedItem();
            if ("General".equals(selectedCategory)) {
                alert();
                return;
            }
            deleteCategory(selectedCategory, true);
        }

        private void deleteCategory(String categoryName, boolean deleteTasks) throws SQLException {
            categoryDAO.deleteCategory(categoryName, userID, deleteTasks);
            if (selectedPage.getText().equals("Category")) {
                handleMyDayButton();
            }
            loadTasks();
            loadCategories();
            categoryListView.getSelectionModel().clearSelection();
        }

        private void alert() {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText(null);
            alert.setContentText("General category cannot be deleted.");
            alert.showAndWait();
        }

        private void setupStatusComboBox() {
            ObservableList<Status> statusList = FXCollections.observableArrayList(Status.values());
            statusComboBox.setItems(statusList);
        }

        private void setupSearchField() {
            currentTasks = FXCollections.observableArrayList();
            currentTasks.addAll(tasks.getTasks());
            FilteredList<TaskImpl> filteredList = new FilteredList<>(currentTasks, task -> true);

            searchField.textProperty().addListener((observable, oldValue, newValue) -> filteredList.setPredicate(task -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return task.getName().toLowerCase().contains(lowerCaseFilter)
                        || task.getDescription().toLowerCase().contains(lowerCaseFilter);
            }));
            taskListView.setItems(filteredList);
        }

        private void setupTaskListView(){
            VBox placeholderLabel = createPlaceholderLabel();
            taskListView.setPlaceholder(placeholderLabel);
            taskListView.setItems(tasks.getTasks());
            setupTasksButtons();
            taskListView.setOnMouseClicked(event -> {
                TaskImpl selectedTask = taskListView.getSelectionModel().getSelectedItem();
                if (selectedTask != null) {
                    if (taskDAO.isCollaborativeTask(selectedTask, selectedTask.getOwner())) {
                        if (collaborationDAO.getPermission(selectedTask.getCategory(), selectedTask.getOwner(), userID).equals(Permission.Read)) {
                            displayTaskInfo(selectedTask);
                            infoPane.setVisible(true);
                            mainPane.setDisable(true);
                        }else{
                            handleEditTask(selectedTask);
                        }
                    }else {
                        handleEditTask(selectedTask);
                    }
                }
            });
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

        private void setupCategoryListView() {
            categoryListView.setItems(categories);
            categoryListView.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY) {
                    String selectedCategory = categoryListView.getSelectionModel().getSelectedItem();
                    if (selectedCategory != null) {
                        try {
                            loadCategoryPage(selectedCategory);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });
        }

        private void setupCategoryButtons() {
            categoryListView.setCellFactory(param -> new ListCell<>() {
                @Override
                protected void updateItem(String category, boolean empty) {
                    super.updateItem(category, empty);
                    if (empty || category == null) {
                        setGraphic(null);
                        setText(null);
                        setStyle("-fx-background-color: transparent;");
                        setOnMouseEntered(null);
                        setOnMouseExited(null);
                    } else {
                        HBox container = createCategoryContainer(category);
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

        private void setupProfileCategories(){
            profileCategories.setCellFactory(param -> new ListCell<>(){
                @Override
                protected void updateItem(String category, boolean empty) {
                    super.updateItem(category, empty);
                    if (empty || category == null){
                        setGraphic(null);
                        setText(null);
                        setStyle("-fx-background-color: transparent;");
                        setOnMouseEntered(null);
                        setOnMouseExited(null);
                    }else{
                        boolean isCollaboratedCategory = category.contains("(") && category.contains(")") &&
                                !category.substring(category.lastIndexOf("(") + 1, category.lastIndexOf(")")).equals(String.valueOf(userID));
                        if (!isCollaboratedCategory){
                            HBox container = createProfileCategoryContainer(category);
                            setGraphic(container);
                            setText("");
                            setPrefHeight(Region.USE_COMPUTED_SIZE);
                            setStyle("-fx-background-color: #6b2fbc; -fx-cursor: hand;");
                            setOnMouseEntered(event -> setStyle("-fx-background-color: #7c3aed;"));
                            setOnMouseExited(event -> setStyle("-fx-background-color: #6b2fbc;"));
                        }
                    }
                }
            });
        }

        private HBox createProfileCategoryContainer(String category){
            HBox container = new HBox();
            container.setSpacing(5);
            Label nameLabel = createLabel(category, "-fx-text-fill: white; -fx-font-size: 14px;");
            nameLabel.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(nameLabel, Priority.ALWAYS);
            Label numberOfTasks = createLabel(String.valueOf(categoryDAO.getTaskCount(category, userID)), "-fx-text-fill: #ffffff; -fx-font-size: 14px; -fx-font-weight: bold;");
            container.getChildren().addAll(nameLabel, numberOfTasks);
            container.setAlignment(Pos.CENTER_LEFT);
            return container;
        }

        private HBox createCategoryContainer(String category) {
            HBox container = new HBox();
            container.setSpacing(5);
            Label nameLabel = createLabel(category, "-fx-text-fill: white; -fx-font-size: 14px;");
            nameLabel.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(nameLabel, Priority.ALWAYS);
            boolean isCollaboratedCategory = category.contains("(") && category.contains(")") &&
                    !category.substring(category.lastIndexOf("(") + 1, category.lastIndexOf(")")).equals(String.valueOf(userID));
            Button addCollaboration;
            Button removeCollaboration = null;
            if (!isCollaboratedCategory) {
                // Owner View
                addCollaboration = createButton("/org/openjfx/miniprojet/assets/images/plus.png", "#FF6F61", event -> handleAddCollaborationButton(category));
                if (collaborationDAO.isCollabCategory(category, userID)){
                    removeCollaboration = createButton("/org/openjfx/miniprojet/assets/images/minus.png", "#FF6F61", event -> handleRemoveCollaborationButton(category));
                }
                container.getChildren().addAll(nameLabel, addCollaboration);
                if (removeCollaboration != null){
                    container.getChildren().add(removeCollaboration);
                }
            } else {
                // Collaborated user view
                removeCollaboration = createButton("/org/openjfx/miniprojet/assets/images/minus.png", "#FF6F61", event -> handleLeaveCollaborationButton(category));
                container.getChildren().addAll(nameLabel, removeCollaboration);
            }
            container.setAlignment(Pos.CENTER_LEFT);
            return container;
        }

        private void handleLeaveCollaborationButton(String category) {
            String categoryName = category.substring(0, category.indexOf("(")).trim();
            String owner = category.substring(category.indexOf("(") + 1, category.indexOf(")")).trim();
            if (confirmLeaving(categoryName)){
                collaborationDAO.removeCollaboration(categoryName, owner, userID);
            }
            latestTaskName = categoryName;
            showNotification("You have left the collaboration", "Collaboration", "You have left the collaboration", categoryName);
            loadCategories();
        }

        private boolean confirmLeaving(String category) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Leave Collaboration");
            alert.setHeaderText("Are you sure you want to leave this collaboration?");
            Optional<ButtonType> result = alert.showAndWait();
            return result.isPresent() && result.get() == ButtonType.OK;
        }

        private void handleRemoveCollaborationButton(String category) {
            handleRemoveCollaborationDialog(category);
            loadCategories();
        }

        private void handleRemoveCollaborationDialog(String category) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/openjfx/miniprojet/assets/fxml/RemoveCollaborationForm.fxml"));
            Parent root;
            try {
                root = loader.load();
                RemoveCollaborationController removeCollaborationController = loader.getController();
                Stage addCollaborationStage = new Stage();
                addCollaborationStage.setTitle("Remove Collaboration");
                addCollaborationStage.setScene(new Scene(root));
                addCollaborationStage.initStyle(StageStyle.UTILITY);
                addCollaborationStage.initModality(Modality.APPLICATION_MODAL);
                removeCollaborationController.setOwner(userID);
                removeCollaborationController.setCategory(category);
                removeCollaborationController.setRemoveCollaborationStage(addCollaborationStage);
                removeCollaborationController.setAppController(this);
                addCollaborationStage.showAndWait();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private void handleAddCollaborationButton(String category) {
            handleAddCollaborationDialog(category);
        }

        private void handleAddCollaborationDialog(String category) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/openjfx/miniprojet/assets/fxml/AddCollaborationForm.fxml"));
            Parent root;
            try {
                root = loader.load();
                AddCollaborationController addCollaborationController = loader.getController();
                Stage addCollaborationStage = new Stage();
                addCollaborationStage.setTitle("Add Collaboration");
                addCollaborationStage.setScene(new Scene(root));
                addCollaborationStage.initStyle(StageStyle.UTILITY);
                addCollaborationStage.initModality(Modality.APPLICATION_MODAL);
                addCollaborationController.setUserID(userID);
                addCollaborationController.setCategory(category);
                addCollaborationController.setAppController(this);
                addCollaborationController.setCollaborationStage(addCollaborationStage);
                addCollaborationStage.showAndWait();
                categoryListView.refresh();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private void setupTasksButtons() {
            taskListView.setCellFactory(param -> new ListCell<>() {
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

        private void setupNotificationButtons() {
            notificationListView.setCellFactory(param -> new ListCell<>() {
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
            Button markAsReadButton = createButton("/org/openjfx/miniprojet/assets/images/check.png", "#00FF7F", event -> {
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
            Button deleteButton = createButton("/org/openjfx/miniprojet/assets/images/remove.png", "#FF6F61", event -> {
                if (taskDAO.isCollaborativeTask(task, task.getOwner())){
                    System.out.println("Collaborative Task");
                    setLatestTaskName(task.getName());
                    showNotification("You do not have permission to delete this task.", "Permission Denied", "You do not have permission to delete this task", task.getName());
                    return;
                }
                deletionAlert(task);
                taskListView.refresh();
            });
            Button checkButton = createButton("/org/openjfx/miniprojet/assets/images/check.png", "#00FF7F", event -> {
                if (task.getStatus().equals(Status.Completed)) {
                    task.changeStatus(Status.Started);
                    try {
                        userDAO.decrementCompletedTasks(userID);
                        userDAO.updateCompletionRate(userID);
                        updateTask(task);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    return;
                }
                if (task.getStatus().equals(Status.Abandoned)){
                    try {
                        userDAO.decrementAbandonedTasks(userID);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
                task.changeStatus(Status.Completed);
                try {
                    userDAO.incrementCompletedTasks(task.getOwner());
                    userDAO.updateCompletionRate(task.getOwner());
                    taskDAO.setCompletionDate(task);
                    updateTask(task);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                taskListView.refresh();
                showNotification("Task completed successfully.", "Task", "has been completed", task.getName());
                // notification sound
                notificationSoundController.playCompletedTaskSound();
            });

            Button editButton = createButton("/org/openjfx/miniprojet/assets/images/pen.png", "#FFD700", event -> {
                if (taskDAO.isCollaborativeTask(task, task.getOwner())){
                    if (collaborationDAO.getPermission(task.getCategory(), task.getOwner(), userID).equals(Permission.Read)){
                        setLatestTaskName(task.getName());
                        showNotification("You do not have permission to edit this task.", "Permission Denied", "You do not have permission to edit this task", task.getName());
                    }else{
                        handleEditTask(task);
                    }
                }else{
                    handleEditTask(task);
                }
            });
            Button infoButton = createButton("/org/openjfx/miniprojet/assets/images/information.png", "#00FF7F", event -> {
                displayTaskInfo(task);
                mainPane.setDisable(true);
                infoPane.setVisible(true);
            });
            Button commentButton = createButton("/org/openjfx/miniprojet/assets/images/comment.png", "#FFD700", event -> {
                try {
                    if (taskDAO.isCollaborativeTask(task, task.getOwner())) {
                        if (collaborationDAO.getPermission(task.getCategory(), task.getOwner(), userID).equals(Permission.Read)) {
                            setLatestTaskName(task.getName());
                            showNotification("You do not have permission to add comments to this task.", "Permission Denied", "You do not have permission to add comments to this task", task.getName());
                            return;
                        }
                    }
                    handleCommentButton(task);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            Button importantButton = createButton(task.isImportant() ? "/org/openjfx/miniprojet/assets/images/starFill.png" : "/org/openjfx/miniprojet/assets/images/star24.png", "#FF6F61", event -> {
                if (taskDAO.isCollaborativeTask(task, task.getOwner())){
                    if (collaborationDAO.getPermission(task.getCategory(), task.getOwner(), userID).equals(Permission.Read)){
                        setLatestTaskName(task.getName());
                        showNotification("You do not have permission to make this task important.", "Permission Denied", "You do not have permission to view this task", task.getName());
                        return;
                    }
                }
                task.setImportant(!task.isImportant());
                try {
                    updateTask(task);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                taskListView.refresh();
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

        private void showAddCommentDialog(TaskImpl task) throws IOException {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/openjfx/miniprojet/assets/fxml/AddCommentForm.fxml"));
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

        private Button createButton(String imagePath, String hoverColor, EventHandler<ActionEvent> action) {
            Button button = new Button();
            button.setPrefSize(30, 30);
            button.setMaxSize(30, 30);
            ImageView icon = new ImageView(new Image(Objects.requireNonNull(getClass().getResource(imagePath)).toString()));
            StackPane iconContainer = new StackPane(icon);
            iconContainer.setAlignment(Pos.CENTER);
            button.setGraphic(iconContainer);
            button.setStyle("-fx-background-color: transparent; -fx-border-radius: 50%; -fx-background-radius: 50%; -fx-padding: 0; -fx-border-width: 2; -fx-cursor: hand; -fx-border-color: #d4d5d5;");
            button.setOnMouseEntered(event -> button.setStyle("-fx-background-color: transparent; -fx-border-radius: 50%; -fx-background-radius: 50%; -fx-padding: 0; -fx-border-width: 2 2 2 2; -fx-border-color: " + hoverColor));
            button.setOnMouseExited(event -> button.setStyle("-fx-background-color: transparent; -fx-border-radius: 50%; -fx-background-radius: 50%; -fx-padding: 0; -fx-border-width: 2 2 2 2; -fx-border-color: #d4d5d5"));
            button.setOnAction(action);
            return button;
        }

        private void handleEditTask(TaskImpl task) {
            TranslateTransition slider = new TranslateTransition(Duration.seconds(0), editForm);
            slider.setToX(0);
            slider.setOnFinished(event -> {
                editForm.setVisible(true);
                page.setPadding(new Insets(0, 320, 0, 0));
                searchField.setVisible(false);
                populateEditForm(task);
            });
            slider.play();
            this.selectedTask = task;
        }

        private void populateEditForm(TaskImpl task) {
            taskNameField.setText(task.getName());
            taskDescriptionField.setText(task.getDescription());
            taskStartDateField.setValue(task.getStartDate());
            TaskDueDateField.setValue(task.getDueDate());
            getTaskReminder(task);
            priorityGroup.selectToggle(getPriorityToggle(task.getPriority()));
            categoryComboBox.setValue(task.getCategory());
            isTaskCompleted = task.getStatus().equals(Status.Completed);
            isTaskAbandoned = task.getStatus().equals(Status.Abandoned);
            statusComboBox.setValue(task.getStatus());
            latestStatus = task.getStatus();
            categoryComboBox.setDisable(taskDAO.isCollaborativeTask(task, task.getOwner()));
        }

        private void getTaskReminder(TaskImpl task){
            System.out.println(task.getReminder());
            switch (task.getReminder()){
                case 1 -> taskReminder.setValue("1 time per week");
                case 3 -> taskReminder.setValue("3 times per week");
                case 7 -> taskReminder.setValue("7 times per week");
            }
        }

        @FXML
        public void handleSaveButton() throws SQLException {
            if (saveChangesAlert()){
                if (selectedTask != null) {
                    updateTaskFields(selectedTask);
                    updateTask(selectedTask);
                    latestTaskName = selectedTask.getName();
                    showNotification("Task updated successfully.", "Task", "has been updated", selectedTask.getName());
                    handleCancelButton();
                }
            }
        }

        private void updateTaskFields(TaskImpl task) throws SQLException {
            String taskName = taskNameField.getText();
            String taskDescription = taskDescriptionField.getText();
            LocalDate taskStartDate = taskStartDateField.getValue();
            LocalDate taskDueDate = TaskDueDateField.getValue();
            String categoryName = categoryComboBox.getValue();
            String taskPriority = ((JFXRadioButton) priorityGroup.getSelectedToggle()).getText();
            Status taskStatus = statusComboBox.getValue();
            int reminder = getTaskReminderNumber(taskReminder.getValue());
            if (isTaskCompleted && !taskStatus.equals(Status.Completed)) {
                userDAO.decrementCompletedTasks(userID);
                userDAO.updateCompletionRate(userID);
            } else if (!isTaskCompleted && taskStatus.equals(Status.Completed)) {
                userDAO.incrementCompletedTasks(userID);
                userDAO.updateCompletionRate(userID);
                taskDAO.setCompletionDate(task);
            }
            if (isTaskAbandoned && !taskStatus.equals(Status.Abandoned)) {
                userDAO.decrementAbandonedTasks(userID);
            } else if (!isTaskAbandoned && taskStatus.equals(Status.Abandoned)) {
                userDAO.incrementAbandonedTasks(userID);
            }
            task.editTask(taskName, taskDescription, taskDueDate, taskPriority, categoryName, taskStartDate, taskStatus, reminder);
        }

        private int getTaskReminderNumber(String reminder){
            switch (reminder) {
                case "1 time per week" -> { return 1; }
                case "3 times per week" -> { return 3; }
                case "7 times per week" -> { return 7; }
                default -> { return 0; }
            }
        }

        public void updateTask(TaskImpl task) throws SQLException {
            boolean isOverRidden = latestStatus != task.getStatus();
            if (taskDAO.isCollaborativeTask(task, task.getOwner())){
                taskDAO.updateCollaborativeTask(task, task.getOwner(), isOverRidden);
            }else{
                taskDAO.updateTask(task, userID, isOverRidden);
            }
            reminderService.resetReminder(task.getId());
            loadTasks();
        }

        @FXML
        public void handleDeleteButton() throws SQLException {
            if (!taskDAO.isCollaborativeTask(selectedTask, selectedTask.getOwner())){
                deleteTask(selectedTask);
                tasks.deleteTask(selectedTask);
                handleCancelButton();
            }else{
                setLatestTaskName(selectedTask.getName());
                handleCancelButton();
                showNotification("You do not have permission to delete this task.", "Permission Denied", "You do not have permission to delete this task", latestTaskName);
            }
        }

        private void deleteTask(TaskImpl task) throws SQLException {
            if (taskDAO.isCollaborativeTask(task, task.getOwner())){
                taskDAO.deleteCollaborativeTask(task, task.getOwner());
            }else{
                taskDAO.deleteTask(task);
                tasks.deleteTask(task);
                if (!task.getStatus().equals(Status.Completed) && !task.getStatus().equals(Status.Abandoned)){
                    userDAO.incrementDeletedTasks(userID);
                }
            }
            setLatestTaskName(task.getName());
            showNotification("Task deleted successfully.", "Task", "has been deleted", latestTaskName);
            loadTasks();
        }

        public void showNotification(String message, String part1, String part2, String taskName) {
            if (latestTaskName != null){
                System.out.println("message: " + message);
                System.out.println("part1: " + part1);
                System.out.println("part2: " + part2);
                System.out.println("taskName: " + taskName);
                System.out.println("userID: " + userID);
                notificationDAO.insertNotification(userID, part1 + " " + taskName + " " + part2, message);
                notificationMessage.setText(message);
                notificationTaskName.setText(part1 + " \"" + taskName + "\" " + part2);
                notificationForm.setVisible(true);
                notificationForm.setTranslateX(300);
                notificationForm.setTranslateY(0);
                TranslateTransition sliderIn = getTranslateTransition();
                sliderIn.play();
                latestTaskName = null;
                loadNotifications();
            }
        }

        @FXML
        public void loadNotifications() {
            List<Notification> notifications = notificationDAO.getNotificationsByUser(userID);
            //notificationNumber.setText(String.valueOf(notifications.size()));todo
            notificationListView.setItems(FXCollections.observableArrayList(notifications));
        }

        private TranslateTransition getTranslateTransition() {
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
            return sliderIn;
        }

        @FXML
        public void handleCancelButton() {
            editForm.setVisible(false);
            page.setPadding(new Insets(0));
            searchField.setVisible(true);
        }

        private JFXRadioButton getPriorityToggle(String priority){
            return switch (priority) {
                case "High" -> high;
                case "Medium" -> medium;
                case "Low" -> low;
                default -> null;
            };
        }

        private void loadCategoryPage(String selectedCategory) throws SQLException {
            searchField.clear();
            handleCancelButton();
            loadPage(selectedCategory);
            loadTasks();
        }

        private void loadPage(String selectedCategory) {
            if (selectedPage != null){
                selectedPage.getStyleClass().remove(SELECTED_PAGE_CLASS);
            }
            notificationPage.setVisible(false);
            page.setVisible(true);
            selectedPage = new JFXButton("Category");
            updateBackground(false);
            updatePageIcon("home-32px.png");
            pageTitle.setText(selectedCategory);
        }

        @FXML
        public void handleMyDayButton() throws SQLException {
            updatePage(myDayButton, "sun32.png", "My Day", true);
            loadTasks();
            page.setVisible(true);
            notificationPage.setVisible(false);
            categoryListView.getSelectionModel().clearSelection();
        }

        @FXML
        public void handleImportantButton() throws SQLException {
            updatePage(importantButton, "star-32px.png", "Important", false);
            loadTasks();
            page.setVisible(true);
            notificationPage.setVisible(false);
            categoryListView.getSelectionModel().clearSelection();
        }

        @FXML
        public void handleTasksButton() throws SQLException {
            updatePage(tasksButton, "home-32px.png", "Tasks", false);
            loadTasks();
            page.setVisible(true);
            notificationPage.setVisible(false);
            categoryListView.getSelectionModel().clearSelection();
        }

        @FXML
        public void handleNotificationButton() {
            updatePage(notificationButton, "notificationx32.png", "Notifications", false);
            page.setVisible(false);
            notificationPage.setVisible(true);
            loadNotifications();
            categoryListView.getSelectionModel().clearSelection();
        }

        private void updatePage(JFXButton newButton, String imageName, String title, boolean isMyDay) {
            if (selectedPage != null) {
                selectedPage.getStyleClass().remove(SELECTED_PAGE_CLASS);
            }
            selectedPage = newButton;
            newButton.getStyleClass().add(SELECTED_PAGE_CLASS);

            updateBackground(isMyDay);
            updatePageIcon(imageName);
            pageTitle.setText(title);
        }

        private void updateBackground(boolean isMyDay) {
            if (isMyDay) {
                page.getStyleClass().remove(PAGE_BACKGROUND_CLASS);
                page.getStyleClass().add(MYDAY_BACKGROUND_CLASS);
            } else {
                page.getStyleClass().add(PAGE_BACKGROUND_CLASS);
                page.getStyleClass().remove(MYDAY_BACKGROUND_CLASS);
            }
        }

        private void updatePageIcon(String imageName) {
            String imageUrl = Objects.requireNonNull(getClass().getResource(IMAGE_PATH + imageName)).toString();
            Image image = new Image(Objects.requireNonNull(imageUrl));
            pageIcon.setImage(image);
        }

        @FXML
        public void handleAddButton() throws IOException, SQLException {
            showAddTaskDialog();
            showNotification("Task added successfully.", "Task", "has been added", latestTaskName);
            loadTasks();
        }

        public void loadTasks() throws SQLException {
            tasks.setTasks(taskDAO.loadTasks(userID, selectedPage.getText(), pageTitle.getText()));
            if (selectedPage.getText().equals("Category")) {
                if (pageTitle.getText().contains("(") && pageTitle.getText().contains(")")) {
                    String fullCategoryName = pageTitle.getText();
                    String categoryName = fullCategoryName.substring(0, fullCategoryName.lastIndexOf("(")).trim();
                    String owner = fullCategoryName.substring(
                            fullCategoryName.lastIndexOf("(") + 1,
                            fullCategoryName.lastIndexOf(")")
                    );
                    ObservableList<TaskImpl> collaborationTasks = taskDAO.loadCollabTasksByCategory(userID, categoryName, owner);
                    tasks.getTasks().addAll(collaborationTasks);
                }
            }
            currentTasks.setAll(tasks.getTasks());
        }

        @FXML
        public void handleAddCategory() throws IOException {
            showAddCategoryDialog();
            showNotification("Category added successfully.", "Category", "has been added", latestTaskName);
        }

        private void showAddTaskDialog() throws IOException {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/openjfx/miniprojet/assets/fxml/AddTaskForm.fxml"));
            Parent root = loader.load();
            AddTaskController addTaskController = loader.getController();
            Stage addTaskStage = new Stage();
            addTaskStage.setTitle("Add Task");
            addTaskStage.setScene(new Scene(root));
            addTaskStage.initStyle(StageStyle.UTILITY);
            addTaskStage.initModality(Modality.APPLICATION_MODAL);
            addTaskController.setAddTaskStage(addTaskStage);
            addTaskController.setAppController(this);
            addTaskController.setUserID(userID);
            addTaskStage.showAndWait();
        }

        private void showAddCategoryDialog() throws IOException {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/openjfx/miniprojet/assets/fxml/AddCategoryForm.fxml"));
            Parent root = loader.load();
            AddCategoryController addCategoryController = loader.getController();
            Stage addCategoryStage = new Stage();
            addCategoryStage.setTitle("Add Category");
            addCategoryStage.setScene(new Scene(root));
            addCategoryStage.initStyle(StageStyle.UTILITY);
            addCategoryStage.initModality(Modality.APPLICATION_MODAL);
            addCategoryController.setAddCategoryStage(addCategoryStage);
            addCategoryController.setAppController(this);
            addCategoryController.setUserID(userID);
            addCategoryStage.showAndWait();
        }

        public void setLatestTaskName(String latestTaskName){
            this.latestTaskName = latestTaskName;
        }

        public void loadCategories() {
            categories.clear();
            categories = categoryDAO.loadCategories(userID);
            categoryComboBox.setItems(categories);
            profileCategories.setItems(categories);
            categories.addAll(categoryDAO.loadCollabCategories(userID));
            categoryListView.setItems(categories);
            categoryMenu.getItems().clear();
            getCategoryMenu();
        }

        public void setUser(String userName) throws SQLException {
            this.userID = userName;
            user.setText(userName);
            setupUserImage();
            selectedPage = myDayButton;
            loadTasks();
            loadNotifications();
            loadCategories();
            setupWeeklyActivityChart();
            reminderService = new TaskReminderService(taskDAO, this);
            reminderService.startReminderService();
            taskListView.getItems().addListener((ListChangeListener<TaskImpl>) c -> setupWeeklyActivityChart());
        }

        public String getUser() {
            return userID;
        }

        private void setupUserImage() {
            String initials = userID.substring(0, 2).toUpperCase();
            imageLabel.setText(initials);
            profileName.setText(initials);
        }

        public void setComment(String comment){
            this.comment = comment;
        }


        @FXML
        public void handleSignOutButton(ActionEvent event) throws IOException, SQLException {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/openjfx/miniprojet/assets/fxml/LandingPage.fxml"));
            Parent root = loader.load();
            userDAO.deleteUser();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("ToDo App");
            stage.show();
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();
        }

        @FXML
        public void handleProfile() throws SQLException {
            profilePane.setVisible(true);
            mainPane.setDisable(true);
            totalTasks.setText(String.valueOf(userDAO.getTotalTasks(userID)));
            completedTasks.setText(String.valueOf(userDAO.getCompletedTasks(userID)));
            abandonedTasks.setText(String.valueOf(userDAO.getAbandonedTasks(userID)));
            deletedTasks.setText(String.valueOf(userDAO.getDeletedTasks(userID)));
            completionRate.setText(userDAO.getCompletionRate(userID) + "%");
            setupProfileCategories();
        }

        @FXML
        public void handleCloseProfile(){
            mainPane.setDisable(false);
            profilePane.setVisible(false);
        }

        private void deletionAlert(TaskImpl task){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText("Are you sure you want to delete this task?");
            alert.setContentText("This action cannot be undone.");
            if (task.getStatus().equals(Status.Completed)){
                alert.setHeaderText("Are you sure you want to delete this task?");
                alert.setContentText("This action cannot be undone. Your completion chart will be affected.");
            }
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK){
                try {
                    deleteTask(task);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        private boolean saveChangesAlert(){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText("Are you sure you want to save changes?");
            alert.setContentText("This action cannot be undone.");
            Optional<ButtonType> result = alert.showAndWait();
            return result.get() == ButtonType.OK;
        }

        public boolean sendInviteAlert(String user){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText("Are you sure you want to send an invite to " + user + "?");
            alert.setContentText("This action cannot be undone.");
            Optional<ButtonType> result = alert.showAndWait();
            return result.get() == ButtonType.OK;
        }

        private void pendingInvitesAlert(ObservableList<String> pendingInvites) {
            for (String invite : pendingInvites) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Invitation Dialog");
                alert.setHeaderText("You have pending invites.");
                alert.setContentText("You have a pending invite from " + invite);

                // Add OK and CANCEL buttons to the alert
                alert.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

                // Show the alert and wait for the user's response
                Optional<ButtonType> result = alert.showAndWait();

                if (result.isPresent()) {
                    if (result.get() == ButtonType.OK) {
                        System.out.println("User accepted the invite: " + invite);
                        handleAcceptInvite(invite);
                    } else if (result.get() == ButtonType.CANCEL) {
                        System.out.println("User declined the invite: " + invite);
                        handleDeclineInvite(invite);
                    }
                } else {
                    System.out.println("User did not interact with the dialog.");
                }
            }
        }

        private void handleAcceptInvite(String invite) {
            // The invite message will be "CategoryName from Owner" let's extract it
            String categoryName = invite.substring(0, invite.lastIndexOf(" from"));
            String owner = invite.substring(invite.lastIndexOf(" from") + 5).trim();
            System.out.println("Category: " + categoryName + " Owner: " + owner);
            try {
                collaborationDAO.acceptInvite(categoryName, owner, userID);
                loadCategories();
                loadTasks();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        private void handleDeclineInvite(String invite) {
            // The invite message will be "CategoryName from Owner" let's extract it
            String categoryName = invite.substring(0, invite.lastIndexOf(" from"));
            String owner = invite.substring(invite.lastIndexOf(" from") + 5).trim();
            collaborationDAO.declineInvite(categoryName, owner, userID);
        }

        public void handleResetAutoStatus() throws SQLException {
            taskDAO.resetToAutoStatus(selectedTask);
            handleCancelButton();
            latestTaskName = selectedTask.getName();
            showNotification("Task status reset successfully.", "Task", "status has been reset", selectedTask.getName());
            loadTasks();
            taskListView.refresh();
        }

        public void handleExportButton() {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Export Tasks");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
            File file = fileChooser.showSaveDialog(new Stage());
            if (file != null) {
                taskDAO.exportTasks(file, userID);
                latestTaskName = "Tasks";
                showNotification("Tasks exported successfully.", "Tasks", "have been exported", "");
            }
        }

        public void handleImportButton() throws SQLException {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Import Tasks");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
            File file = fileChooser.showOpenDialog(new Stage());
            if (file != null) {
                taskDAO.importTasks(file, userID);
                latestTaskName = "Tasks";
                showNotification("Tasks imported successfully.", "Tasks", "have been imported", "");
                loadTasks();
                loadCategories();
            }
        }

        private void checkUpcomingTasks() {
            taskDAO.checkUpcomingTasks(userID, this);
        }

        private void checkOverdueTasks() {
            taskDAO.checkOverdueTasks(userID, this);
        }

        public void alertUpComingTasks(String taskName, String message) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Upcoming Task");
            alert.setHeaderText("Upcoming Task: " + taskName);
            alert.setContentText(message);
            ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            alert.getButtonTypes().setAll(okButton);
            alert.showAndWait();
        }

        public void alertOverdueTasks(String taskName, String message) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Overdue Task");
            alert.setHeaderText("Overdue Task: " + taskName);
            alert.setContentText(message);
            ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            alert.getButtonTypes().setAll(okButton);
            alert.showAndWait();
        }
    }